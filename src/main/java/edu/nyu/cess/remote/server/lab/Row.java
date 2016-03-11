package edu.nyu.cess.remote.server.lab;

import java.util.List;

/**
 * Created by aruff on 2/24/16.
 */
public class Row
{
	public int number;
	public List<Computer> computers;

	public int getNumber()
	{
		return number;
	}

	public void setNumber(int number)
	{
		this.number = number;
	}

	public List<Computer> getComputers()
	{
		return computers;
	}

	public void setComputers(List<Computer> computers)
	{
		this.computers = computers;
	}
}
