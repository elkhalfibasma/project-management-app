import { Component, OnInit } from '@angular/core';
import { ApiService } from '../services/api.service';
import { WsService } from '../services/ws.service';

@Component({ selector: 'app-tasks', templateUrl: './tasks.page.html' })
export class TasksPage implements OnInit {
  list:any[]=[];
  constructor(private api: ApiService, private ws: WsService){}
  async ngOnInit(){
    this.list = await this.api.get('/tasks');
    this.ws.onTask((m:any)=>{
      if(m?.type==='created'){ /* reload single item server-side in future */ }
      if(m?.type==='updated'){ /* no-op for now */ }
      if(m?.type==='deleted'){ this.list = this.list.filter(t=>t.id!==m.id); }
    });
  }
}
