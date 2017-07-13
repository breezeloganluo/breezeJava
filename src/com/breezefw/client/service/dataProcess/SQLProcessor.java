package com.breezefw.client.service.dataProcess;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.breezefw.client.service.ProjectMgr;

public class SQLProcessor extends DataProcessAbs {
	private StringBuilder sqlBuilder = new StringBuilder();
	private String baseDir;

	public SQLProcessor(String b) {
		this.baseDir = b;
	}

	@Override
	public void doData(String data, String fileDir) {
		sqlBuilder.append(data);
	}

	@Override
	public void finished() {
		try {
			String builderDir = this.baseDir + ProjectMgr.basePrifix;
			(new File(builderDir)).mkdirs();
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(
					builderDir + "sql.sql"));
			System.out.println("finished data is:" + this.sqlBuilder.toString());
			out.write(this.sqlBuilder.toString().getBytes("UTF-8"));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
