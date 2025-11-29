import { Component, OnInit } from '@angular/core';
import { ApiService } from '../services/api.service';

@Component({ selector: 'app-users', templateUrl: './users.page.html' })
export class UsersPage implements OnInit {
  list:any[]=[];
  constructor(private api: ApiService){}
  async ngOnInit(){ this.list = await this.api.get('/users'); }
}
