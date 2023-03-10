
export class Objects {
    
  /**
   * 
   */
  private constructor() {
  }
  
  /**
   * 
   */
  public static isNull(obj:any):boolean {
    return obj === undefined || obj === null;
  }

  /**
   * 
   */
  public static isNotNull(obj:any):boolean {
    return !this.isNull(obj);
  }
  
  
  /**
   * 
   */
  public static notNull<T>(arg:T, message:string):T {
    if (Objects.isNotNull(arg)) {
      return arg;
    } else {
      throw new Error(message); 
    }
  }
}
