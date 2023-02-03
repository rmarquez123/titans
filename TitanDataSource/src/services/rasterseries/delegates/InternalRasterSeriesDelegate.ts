import {SeriesDelegate} from './SeriesDelegate';
import {RasterSeries} from 'src/core/rasters/RasterSeries';
import {Observable, BehaviorSubject} from 'rxjs';
import {RasterParameter} from 'src/core/rasters/RasterParameter';

/**
 * 
 */
export class InternalRasterSeriesDelegate implements SeriesDelegate {
  
  /**
   * 
   */
  public getSeries(rasterId: number, point: any, params: RasterParameter[]): //
    Observable<RasterSeries> {
    const result: BehaviorSubject<RasterSeries> = new BehaviorSubject(null);
    setTimeout(() => {
      const data = [];
      for (let i = 0; i < 10; i++) {
        data.push(Math.random());
      }
      const series = new RasterSeries(rasterId, data);
      result.next(series);
    });
    return result;
  }
}