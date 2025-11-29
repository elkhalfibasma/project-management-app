import { Injectable, NgZone } from '@angular/core';
import { Client, IMessage, Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

@Injectable({ providedIn: 'root' })
export class WsService {
  private client?: Client;
  private listeners: ((msg: any)=>void)[] = [];
  constructor(private zone: NgZone) {}

  connect(){
    if (this.client) return;
    this.client = Stomp.over(()=> new SockJS('/ws'));
    this.client.reconnectDelay = 5000;
    this.client.debug = () => {};
    this.client.onConnect = () => {
      this.client?.subscribe('/topic/tasks', (m: IMessage)=>{
        const body = JSON.parse(m.body||'{}');
        this.zone.run(()=> this.listeners.forEach(l => l(body)));
      });
    }
    this.client.activate();
  }

  onTask(listener: (msg:any)=>void){ this.listeners.push(listener); this.connect(); }
}
