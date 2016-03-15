package edu.nyu.cess.remote.common.app;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AppExecutionValidatorTest
{
    @Test
    public void When_InvalidAppExecutionIsInvalidated_ReturnFalse() throws Exception
    {
        assertFalse(AppExecutionValidator.validate(null));
		assertFalse(AppExecutionValidator.getValidationError(null).isEmpty());
        assertFalse(AppExecutionValidator.validate(new AppExe(new AppInfo(null,null,null),null)));
		assertFalse(AppExecutionValidator.getValidationError(new AppExe(new AppInfo(null,null,null),null)).isEmpty());
        assertFalse(AppExecutionValidator.validate(new AppExe(new AppInfo("",null,null),null)));
		assertFalse(AppExecutionValidator.getValidationError(new AppExe(new AppInfo("",null,null),null)).isEmpty());
        assertFalse(AppExecutionValidator.validate(new AppExe(new AppInfo("","",""),AppState.STARTED)));
		assertFalse(AppExecutionValidator.getValidationError(new AppExe(new AppInfo("","",""),AppState.STARTED)).isEmpty());
        assertFalse(AppExecutionValidator.validate(new AppExe(new AppInfo(null,null,null),AppState.STARTED)));
		assertFalse(AppExecutionValidator.getValidationError(new AppExe(new AppInfo(null,null,null),AppState.STARTED)).isEmpty());
    }

    @Test
    public void When_ValidAppExecutionIsInvalidated_ReturnTrue() throws Exception
    {
        assertTrue(AppExecutionValidator.validate(new AppExe(new AppInfo("a","a","a"),AppState.STARTED)));
    }

}
