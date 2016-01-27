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
        assertFalse(AppExecutionValidator.validate(new AppExecution(null,null,null,null)));
        assertFalse(AppExecutionValidator.validate(new AppExecution("",null,null,null)));
        assertFalse(AppExecutionValidator.validate(new AppExecution("","","",AppState.STARTED)));
        assertFalse(AppExecutionValidator.validate(new AppExecution(null,null,null,AppState.STARTED)));
    }

    @Test
    public void When_ValidAppExecutionIsInvalidated_ReturnTrue() throws Exception
    {
        assertTrue(AppExecutionValidator.validate(new AppExecution("a","a","a",AppState.STARTED)));
    }

}