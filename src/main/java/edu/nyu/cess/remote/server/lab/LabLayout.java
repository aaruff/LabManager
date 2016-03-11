package edu.nyu.cess.remote.server.lab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class contains the lab layout information, such as the
 * computer names, IP address, and row they are located in.
 */
public class LabLayout
{
	public List<Row> rows;

	public LabLayout()
	{
		rows = new ArrayList<>();
	}

	public List<Row> getRows()
	{
		return rows;
	}

	public void setRows(List<Row> rows)
	{
		this.rows = rows;
	}

	public ArrayList<Computer> getAllComputers()
	{
		ArrayList<Computer> computerList = new ArrayList<>();
		for (Row row : getRows()) {
			for (Computer computer : row.getComputers()) {
				computerList.add(computer);
			}
		}

		return computerList;
	}

    public HashMap<String, Computer> getComputersByIp()
    {
        HashMap<String, Computer> computerMap = new HashMap<>();
        for (Row row : getRows()) {
            for (Computer computer : row.getComputers()) {
                computerMap.put(computer.getIp(), computer);
            }
        }

        return computerMap;
    }
}
