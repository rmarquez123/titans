import {Point} from 'src/core/types/Point';


export class MapResult {
  
  public readonly upperRight:Point;
  public readonly lowerLeft:Point;  
  public readonly srid:number;
  public readonly imageURL:string;
  
  
  public constructor(imageURL:string, srid:number, upperRight:Point, lowerLeft:Point) {
    this.imageURL = imageURL;
    this.srid = srid;
    this.upperRight = upperRight;
    this.lowerLeft = lowerLeft;
  }
  
  
}