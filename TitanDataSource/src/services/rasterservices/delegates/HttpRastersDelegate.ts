import {RastersDelegate} from './RastersDelegate';
import {RastersService} from '../RastersService';
import {HttpClient, HttpParams, HttpHeaders} from '@angular/common/http';
import {Observable, BehaviorSubject} from 'rxjs';
import {map} from 'rxjs/operators';
import {RastersGroup} from 'src/core/rasters/RastersGroup';
import {RasterParameter} from 'src/core/rasters/RasterParameter';
import {RasterEntity} from 'src/core/rasters/RasterEntity';
import {RasterImage} from 'src/core/rasters/RasterImage';
import {Envelope} from 'src/core/types/Envelope';


export class HttpRastersDelegate implements RastersDelegate {


  private baseUrl: string = "http://localhost:8081/titansdata.web.dev"
  private patterns: Map<number, BehaviorSubject<RasterParameter[]>> = new Map();
  private host: RastersService;

  /**
   * 
   */
  public constructor(private http: HttpClient) {
  }

  /**
   * 
   */
  public setHost(host: RastersService): void {
    this.host = host;
  }

  /**
   * 
   */
  public loadRasters(): void {
    const url = this.baseUrl + "/getRasters";
    const params = new HttpParams().set("userId", "0");
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const options = {params: params, headers: headers};
    this.http.get(url, options).subscribe((response: any) => {
      const values = JSON.parse(response.values).rastergroups;
      const arr = values.map((value: any) => {
        const id = value.rasterGroupId;
        const name = value.name;
        const rasterIds: number[] = value.rasterIds
        const g = new RastersGroup(id, name, rasterIds);
        return g;
      });
      this.host.rasterGroups.next(arr);
    });
  }

  /**
   * 
   */
  public loadRasterEntity(rasterId: number, callback: (v: any) => void): void {
    const url = this.baseUrl + "/getRaster";
    const params = new HttpParams()
      .set("rasterId", rasterId.toString())
      ;
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const options = {params: params, headers: headers};
    this.http.get(url, options).subscribe((response: any) => {
      const v: any = response.value;
      const e = this.mapToEntity(v);
      this.host.rastersmap.set(v.rasterId, e);
      callback(e);
    });
  }

  /**
   * 
   */
  public getAllRasters(): Observable<RastersGroup[]> {
    const url = this.baseUrl + "/getRasters";
    const params = new HttpParams()
      .set("userId", "0")
      ;
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const options = {params: params, headers: headers};
    const result = this.http.get(url, options)
      .pipe<RastersGroup[]>(map(this.responseToRasterGroupArray.bind(this)));
    return result;
  }


  /**
   * 
   */
  private responseToRasterGroupArray(response: any): void {
    const values = JSON.parse(response.values).rastergroups;
    const arr = values.map((value: any) => {
      const id = value.rasterGroupId;
      const name = value.name;
      const rasterIds: number[] = value.rasterIds
      const g = new RastersGroup(id, name, rasterIds);
      return g;
    });
    return arr;
  }

  /**
   * 
   */
  private mapToEntity(value: any): RasterEntity {
    const e = new RasterEntity(value.rasterId);
    return e;
  }



  /**
   * 
   */
  public getRasterImage(param: RasterParameter): Observable<RasterImage> {
    const url = this.baseUrl + "/getRasterImage";
    const rasterId = param.rasterId;
    const parameter = param.parameter;
    const params = new HttpParams()
      .set("rasterId", rasterId.toString())
      .set("parameter", JSON.stringify(parameter))
      ;
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const result: BehaviorSubject<RasterImage> = new BehaviorSubject(null);
    const options = {
      params: params,
      headers: headers
    };
    this.http.get(url, options).subscribe((response: any) => {
      result.next(this.toRasterImage(response));
    });
    return result;
  }

  /**
   * 
   */
  private toRasterImage(response: any): RasterImage {
    const value = response.value;
    const imageUrl = value.imageURL;
    const xmin = value.lowerLeft.x;
    const xmax = value.upperRight.x;
    const ymin = value.lowerLeft.y;
    const ymax = value.upperRight.y;
    const srid = value.srid;
    const envelope: Envelope = new Envelope(xmin, xmax, ymin, ymax, srid);
    const result: RasterImage = new RasterImage(imageUrl, envelope);
    return result;
  }

  /**
   * 
   */
  public getParameters(rasterId: number): Observable<any[]> {
    if (!this.patterns.has(rasterId)) {
      const value = new BehaviorSubject([]);
      const url = this.baseUrl + "/getRasterParameters";
      const params = new HttpParams()
        .set("rasterId", rasterId.toString());
      const headers = new HttpHeaders({
        'Content-Type': 'application/json'
      });
      const options = {params: params, headers: headers};
      this.http.get(url, options).subscribe((response) => {
        const patternsArray = this.responseToPatterns(rasterId, response);
        value.next(patternsArray);
      });
      this.patterns.set(rasterId, value);
    }
    const result = this.patterns.get(rasterId);
    return result;
  }

  /**
   * 
   */
  private responseToPatterns(rasterId: number, response: any): RasterParameter[] {
    const values: any[] = response.values;
    const result = values.map(v => new RasterParameter(rasterId, v.map));
    return result;
  }

}
