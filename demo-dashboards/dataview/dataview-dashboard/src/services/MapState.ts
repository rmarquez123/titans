import {MapResult} from './MapResult';
import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class MapState {
  
  private mapResult:BehaviorSubject<MapResult> = new BehaviorSubject(null);
    
  /**
   * 
   */
  public addMap(mapResult:MapResult):void  {
    this.mapResult.next(mapResult); 
  }
  
  /**
   * 
   */
  public getMapResult():Observable<MapResult> {
    return this.mapResult;
  } 
}


