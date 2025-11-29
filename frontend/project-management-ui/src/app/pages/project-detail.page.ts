import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../services/api.service';

@Component({ selector: 'app-project-detail', templateUrl: './project-detail.page.html' })
export class ProjectDetailPage implements OnInit {
  id!:number; project:any; tasks:any[]=[]; prediction:any; recs:string[]=[];
  constructor(private route: ActivatedRoute, private api: ApiService){}
  async ngOnInit(){
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.project = await this.api.get(`/projects/${this.id}`);
    this.tasks = await this.api.get(`/tasks/by-project/${this.id}`);
    this.prediction = await this.api.get(`/ai/predictDelay/${this.id}`);
    const r = await this.api.get(`/ai/recommendations/${this.id}`);
    this.recs = r.actions || [];
  }
}
