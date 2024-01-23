


/**
 * 
 */
export class Point {
  
  public readonly x:number; 
  public readonly y:number; 
  
  /**
   * 
   */
  public constructor(x:number, y:number) {
    this.x = x; 
    this.y = y; 
  }
    
  /**
   * 
   */
  public static fromObj(obj:any):Point {
    const result = new Point(obj.x, obj.y); 
    return result; 
  }
  
  /**
   * 
   */
  public static create({x, y}:any): Point {
    return new Point(x, y); 
  }
  
}
