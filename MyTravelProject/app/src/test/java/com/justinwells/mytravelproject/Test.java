package com.justinwells.mytravelproject;

import com.justinwells.mytravelproject.Misc.CurrentDate;

/**
 * Created by justinwells on 1/9/17.
 */

public class Test {

    @org.junit.Test
    public void dateTest() throws Exception {
        Boolean b = CurrentDate.isValidDate(CurrentDate.idealFlightDate());

        assert(b);
    }


}
