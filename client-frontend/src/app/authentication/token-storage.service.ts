import { Injectable } from '@angular/core';

const TOKEN_KEY = 'auth-token';
const USER_KEY = 'auth-user';
const TYPE_KEY = 'role-user';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  constructor() { }

  signOut() {
    window.sessionStorage.clear();
  }

  public saveToken(token: string) {
    window.sessionStorage.removeItem(TOKEN_KEY);
    window.sessionStorage.setItem(TOKEN_KEY, token);
  }

  public getToken(): string {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  public saveUsername(username: string) {
    window.sessionStorage.removeItem(USER_KEY);
    window.sessionStorage.setItem(USER_KEY, username);
  }

  public getUsername(): string {
    return sessionStorage.getItem(USER_KEY);
  }

  public saveType(type: string) {
    window.sessionStorage.removeItem(TYPE_KEY);
    window.sessionStorage.setItem(TYPE_KEY, type);
  }

  public getType(): string {
    return sessionStorage.getItem(TYPE_KEY);
  }
}
