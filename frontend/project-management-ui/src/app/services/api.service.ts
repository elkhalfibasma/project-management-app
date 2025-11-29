import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient){}
  async get(path: string){ return await this.http.get<any>(path).toPromise(); }
  async post(path: string, body: any){ return await this.http.post<any>(path, body).toPromise(); }
}
