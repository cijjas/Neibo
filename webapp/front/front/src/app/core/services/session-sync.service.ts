import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class SessionSyncService {
  private channel = new BroadcastChannel('auth-channel');

  notify(event: 'login' | 'logout'): void {
    this.channel.postMessage({ type: event });
  }

  onEvent(callback: (event: { type: string }) => void): void {
    this.channel.onmessage = (event) => callback(event.data);
  }
}
