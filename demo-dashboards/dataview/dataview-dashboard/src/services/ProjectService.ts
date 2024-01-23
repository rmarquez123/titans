import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';


@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  
  private baseUrl: string = "http://localhost:8081/titansdata.web.dev/";
  
  public constructor(private http:HttpClient) {
  }
    
  /**
   * 
   */
  public setProject(projectId: number): void {
    const url = this.baseUrl + "/setProject";
    const params = new HttpParams()
      .set("project_id", projectId + ""); 
    this.http.post(url, {}, {params: params}).subscribe(response => {
      console.log(response);
    });
  }
}


