import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(private http: HttpClient){}
  async login(body: any){
    const res:any = await firstValueFrom(this.http.post('/auth/login', body));
    localStorage.setItem('token', res.token);
  }
  logout(){ localStorage.removeItem('token'); location.href = '/login'; }
  isLoggedIn(){ return !!localStorage.getItem('token'); }
}
