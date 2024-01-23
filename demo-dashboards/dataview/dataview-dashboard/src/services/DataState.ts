import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {Source} from './Source';
import {SourceVariable} from './SourceVariable';
import {MapResult} from './MapResult';
import {Point} from 'src/core/types/Point';

@Injectable({
  providedIn: "root"
})
export class DataState {
  private baseUrl = "http://localhost:8081/titansdata.web.dev/";
  public constructor(private httpClient: HttpClient) {
    this.httpClient.post("login", {

    });
  }

  /**
   * 
   */
  public getSources(): Promise<Source[]> {
    const url = this.baseUrl + "getRasters?userId=0";
    const result = this.httpClient.get(url) //
      .pipe(map(this.responseToSourceArray))//
      .toPromise();
    return result;
  }


  /**
   * 
   */
  public getSourceVariables(rasterId: number): Promise<SourceVariable[]> {
    const url = this.baseUrl + "getRasterClasses?rasterId=" + rasterId;
    const result = this.httpClient.get(url) //
      .pipe(map(this.responseToSourceVariableArray))//
      .toPromise();
    return result;
  }

  /**
   * 
   */
  public getRasterImage(rasterId: number, datetime: string, variable: string): Promise<MapResult> {
    const url = this.baseUrl + "getNoaaRasterImage";
    const params = new HttpParams() //
      .set("rasterId", rasterId + "")  //
      .set("datetime", datetime) //
      .set("zoneId", "UTC") //
      .set("variable", variable);
    const result = this.httpClient.get(url, {params: params}) //
      .pipe(map((this.responseToImage)))//
      .toPromise();
    return result;
  }

  /**
   * 
   */
  private responseToImage(response: any): MapResult {
    console.log(response); 
    const value:any = response.value;
    const imageURL: string = value.imageURL;
    const srid: number = value.srid;
    const upperRight: Point = Point.fromObj(value.upperRight);
    const lowerLeft: Point = Point.fromObj(value.lowerLeft);
    const result = new MapResult(imageURL, srid, upperRight, lowerLeft);
    return result;
  }

  /**
   * 
   */
  private responseToSourceArray(response: any): Source[] {
    const values: [] = JSON.parse(response.values).rastergroups;
    const result = values.map((r: any) => {
      const rasterId: number = r.rasterIds[0];
      const name: string = r.name;
      return new Source(rasterId, name);
    });

    return result;
  }

  /**
   * 
   */
  private responseToSourceVariableArray(response: any): SourceVariable[] {
    const values: [] = response.clazzes.map.NOAA_VAR.myArrayList;
    const result = values.map((r: any) => {
      const s = JSON.parse(r);
      const name = s.varName[0];
      return new SourceVariable(name);
    });
    return result;
  }
}
