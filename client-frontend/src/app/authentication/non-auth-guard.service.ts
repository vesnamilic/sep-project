import { TokenStorageService } from './token-storage.service';
import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';


@Injectable({ providedIn: 'root' })
export class NonAuthGuardService implements CanActivate {
    constructor(
        private router: Router,
        private tokenStorageService: TokenStorageService
    ) { }

    canActivate(): boolean {
      if (!this.tokenStorageService.getToken()) {

        return true;
      }

      this.router.navigate(['/']);

      return false;
    }
}