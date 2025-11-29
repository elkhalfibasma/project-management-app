import { Component, OnInit } from '@angular/core';
import { ApiService } from '../services/api.service';

@Component({ selector: 'app-projects', templateUrl: './projects.page.html' })
export class ProjectsPage implements OnInit {
  list:any[]=[]; form:any={ title:'', description:'', budget:0, status:'PLANIFIE', progress:0 };
  constructor(private api: ApiService){}
  async ngOnInit(){ this.list = await this.api.get('/projects'); }
  async save(){ const p = await this.api.post('/projects', this.form); this.list.push(p); this.form = { title:'', description:'', budget:0, status:'PLANIFIE', progress:0 }; }
}
