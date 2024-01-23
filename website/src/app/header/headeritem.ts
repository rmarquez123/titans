
/**
 * 
 */
export class HeaderItem {
  
  public readonly title:string;
  public readonly path:string;
  public readonly items:HeaderItem[]; 
  
  public constructor(arg: {title:string, path:string, items:HeaderItem[]}) {
    this.title = arg.title; 
    this.path = arg.path; 
    this.items = arg.items; 
  }
  
}

