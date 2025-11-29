import { Component, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import Chart from 'chart.js/auto';
import { ApiService } from '../services/api.service';

@Component({ selector: 'app-dashboard', templateUrl: './dashboard.page.html' })
export class DashboardPage implements AfterViewInit {
  projects:any[]=[]; tasks:any[]=[]; recommendations:string[]=[];
  @ViewChild('kpiChart') kpiChart!: ElementRef<HTMLCanvasElement>;
  constructor(private api: ApiService) {}
  async ngAfterViewInit(){
    this.projects = await this.api.get('/projects');
    this.tasks = await this.api.get('/tasks');
    new Chart(this.kpiChart.nativeElement.getContext('2d')!, { type:'doughnut', data:{ labels:['Done','In Progress','Todo'], datasets:[{ data:[
      this.tasks.filter(t=>t.status==='DONE').length,
      this.tasks.filter(t=>t.status==='IN_PROGRESS').length,
      this.tasks.filter(t=>!['DONE','IN_PROGRESS'].includes(t.status)).length,
    ], backgroundColor:['#16a34a','#06b6d4','#64748b'] }] } });
  }
}
