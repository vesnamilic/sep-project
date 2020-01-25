import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';
import { TokenStorageService } from './token-storage.service';


const TOKEN_HEADER_KEY = 'Authorization';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private tokenStorageService: TokenStorageService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler) {
    let authRequest = request;

    const token = this.tokenStorageService.getToken();
    const type = this.tokenStorageService.getType();

    if (token != null) {
      authRequest = request.clone({ headers: request.headers.set(TOKEN_HEADER_KEY, type + ' ' + token) });
    }

    return next.handle(authRequest);
  }
}
