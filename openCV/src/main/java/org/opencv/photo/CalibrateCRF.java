//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.photo;

import org.opencv.core.Algorithm;
import org.opencv.core.Mat;
import org.opencv.utils.Converters;

import java.util.List;

// C++: class CalibrateCRF
//javadoc: CalibrateCRF

public class CalibrateCRF extends Algorithm {

    protected CalibrateCRF(long addr) {
        super(addr);
    }

    // internal usage only
    public static CalibrateCRF __fromPtr__(long addr) {
        return new CalibrateCRF(addr);
    }

    //
    // C++:  void cv::CalibrateCRF::process(vector_Mat src, Mat& dst, Mat times)
    //

    //javadoc: CalibrateCRF::process(src, dst, times)
    public  void process(List<Mat> src, Mat dst, Mat times)
    {
        Mat src_mat = Converters.vector_Mat_to_Mat(src);
        process_0(nativeObj, src_mat.nativeObj, dst.nativeObj, times.nativeObj);
        
        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  void cv::CalibrateCRF::process(vector_Mat src, Mat& dst, Mat times)
    private static native void process_0(long nativeObj, long src_mat_nativeObj, long dst_nativeObj, long times_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
