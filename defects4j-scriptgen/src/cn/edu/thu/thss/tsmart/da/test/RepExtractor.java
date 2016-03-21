package cn.edu.thu.thss.tsmart.da.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RepExtractor {
	
	
	
	private static final String OUTPUT_SCRIPT = "checkout_all.sh";

	public static void main(String[] args) {
		try {
			
			File scriptfile = new File(Constants.DEFECTS4J_ROOT + "/" + OUTPUT_SCRIPT);
			if(!scriptfile.exists()){
				scriptfile.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(Constants.DEFECTS4J_ROOT + "/" + OUTPUT_SCRIPT)));
			writer.write("export PATH=$PATH:" + Constants.DEFECTS4J_ROOT + "/framework/bin");
			writer.newLine();
			
			genScriptForProgram(Constants.COMMONS_LANG_KEY, Constants.LANG_VER_NUM, writer);
			genScriptForProgram(Constants.COMMONS_MATH_KEY, Constants.MATH_VER_NUM, writer);
			genScriptForProgram(Constants.CLOSURE_COMPILER_KEY, Constants.CLOSURE_VER_NUM, writer);
			genScriptForProgram(Constants.JODA_TIME_KEY, Constants.TIME_VER_NUM, writer);
			genScriptForProgram(Constants.JFREE_CHART_KEY, Constants.CHART_VER_NUM, writer);
			writer.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}



	private static void genScriptForProgram(String programKey, int programVerNum, BufferedWriter scriptWriter) throws IOException{
		for(int i = 1; i <= programVerNum; i ++){
			//buggy version
			String workingDirBuggy = Constants.DEFECTS4J_ROOT + "/" + Constants.CHECKOUT_ROOT + "/" 
					+ programKey + "/v" + i + "/" + Constants.BUGGY;
			ensureDir(workingDirBuggy);
			scriptWriter.write(
					"defects4j checkout -p " + programKey 
					+ " -v " + i + Constants.BUGGY 
					+ " -w " + workingDirBuggy);
			scriptWriter.newLine();
			scriptWriter.write("cd " + workingDirBuggy);
			scriptWriter.newLine();
			scriptWriter.write("defects4j compile");
			scriptWriter.newLine();
			scriptWriter.write("defects4j test");
			scriptWriter.newLine();
			
			//fixed version
			String workingDirFixed = Constants.DEFECTS4J_ROOT + "/" + Constants.CHECKOUT_ROOT + "/" 
					+ programKey + "/v" + i + "/" + Constants.FIXED;
			ensureDir(workingDirFixed);
			scriptWriter.write(
					"defects4j checkout -p " + programKey 
					+ " -v " + i + Constants.FIXED 
					+ " -w " + workingDirFixed);
			scriptWriter.newLine();
			scriptWriter.write("cd " + workingDirFixed);
			scriptWriter.newLine();
			scriptWriter.write("defects4j compile");
			scriptWriter.newLine();
			scriptWriter.write("defects4j test");
			scriptWriter.newLine();
		}
	}

	private static void ensureDir(String dirName) throws IOException{
		File dir = new File(dirName);
		if(!dir.exists()){
			ensureDir(dir.getParent());
			dir.mkdir();
		}
	}
}
