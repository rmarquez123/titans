import {Injectable} from '@angular/core';
import {SeriesProperties} from './SeriesProperties';

@Injectable({
  providedIn: 'root'
  , deps: []
})
export class RasterSeriesPropertiesService {

  private properties: Map<number, SeriesProperties> = new Map();

  /**
   * 
   */
  public getProperties(rasterId: number): SeriesProperties {
    if (!this.properties.has(rasterId)) {
      const p = this.createProperties(rasterId);
      this.properties.set(rasterId, p);
    }
    return this.properties.get(rasterId);
  }

  /**
   * 
   */
  private createProperties(rasterId: number): SeriesProperties {
    const result = new SeriesProperties({color: this.getRandomColor(rasterId)});
    return result;
  }

  /**
   * 
   */
  private getRandomColor(rasterId: number): string {
    const colors = ["#0c0d1f", "#6a70c2", "#3b3e6f"];
    const result = colors[rasterId]; 
    return result;
//    const letters = '0123456789ABCDEF';
//    let color = '#';
//    for (let i = 0; i < 6; i++) {
//      color += letters[Math.floor(Math.random() * 16)];
//    }
//    return color;
  }
}