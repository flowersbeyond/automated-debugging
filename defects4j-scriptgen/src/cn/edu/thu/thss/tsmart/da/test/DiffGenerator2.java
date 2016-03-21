package cn.edu.thu.thss.tsmart.da.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DiffGenerator2 {
	private static String OUTPUT_SCRIPT = "gen_diff_all.sh";
	private static String DIFF_FILE_NAME = "classes_modified_summary";
	private static String DIFF_FILE_LOG = "diff_file_all.log";
	
	private static String LINE_SEPARATOR = "----------------------------------------";

	public static void main(String[] args) {
		try {

			genScriptForProgram(Constants.COMMONS_LANG_KEY,
					Constants.LANG_VER_NUM);
			genScriptForProgram(Constants.COMMONS_MATH_KEY,
					Constants.MATH_VER_NUM);
			genScriptForProgram(Constants.CLOSURE_COMPILER_KEY,
					Constants.CLOSURE_VER_NUM);
			genScriptForProgram(Constants.JODA_TIME_KEY,
					Constants.TIME_VER_NUM);
			genScriptForProgram(Constants.JFREE_CHART_KEY,
					Constants.CHART_VER_NUM);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// all reps should be initialized before running this script,
	// so we don't check whether all working directories exist or not
	private static void genScriptForProgram(String programKey,
			int programVerNum) throws IOException {
		File scriptfile = new File(Constants.DEFECTS4J_ROOT + "/"
				+ Constants.CHECKOUT_ROOT + "/" + programKey + "/"+ OUTPUT_SCRIPT);
		if (!scriptfile.exists()) {
			scriptfile.createNewFile();
		}
		
		File logfile = new File(Constants.DEFECTS4J_ROOT + "/"
				+ Constants.CHECKOUT_ROOT + "/" + programKey + "/"+ DIFF_FILE_LOG);
		if (!logfile.exists()) {
			logfile.createNewFile();
		}
		
		File diff_sum_file = new File(Constants.DEFECTS4J_ROOT + "/"
				+ Constants.CHECKOUT_ROOT + "/" + programKey + "/"+ DIFF_FILE_NAME);
		if (!diff_sum_file.exists()) {
			diff_sum_file.createNewFile();
		}
		
		BufferedWriter scriptwriter = new BufferedWriter(new FileWriter(scriptfile));		
		scriptwriter.write("export PATH=$PATH:" + Constants.DEFECTS4J_ROOT
				+ "/framework/bin");
		scriptwriter.newLine();
		scriptwriter.write("echo \"" + LINE_SEPARATOR + "\" >> " + diff_sum_file.getAbsolutePath());
		scriptwriter.newLine();
		
		BufferedWriter logger = new BufferedWriter(new FileWriter(logfile));
		logger.write(LINE_SEPARATOR);
		logger.newLine();

		for (int i = 1; i <= programVerNum; i++) {
			
			String srcRoot = getSrcRoot(programKey, i);
			
			scriptwriter.write("echo \"v" + i + "\" >> " + diff_sum_file.getAbsolutePath());
			scriptwriter.newLine();
			
			logger.write("v" + i);
			logger.newLine();
			
			// buggy version
			String workingDirBuggy = Constants.DEFECTS4J_ROOT + "/"
					+ Constants.CHECKOUT_ROOT + "/" + programKey + "/v" + i
					+ "/" + Constants.BUGGY;
			
			//read the modified class names
			File class_modified_file = new File(Constants.DEFECTS4J_ROOT + "/"
					+ Constants.CHECKOUT_ROOT + "/" + programKey
					+ "/v" + i + "/" + Constants.BUGGY + "/" + DiffGenerator1.DIFF_FILE_NAME);
			if(!class_modified_file.exists()){
				logger.write("!!!class_modified file not found!!! skip.");
				logger.newLine();
				logger.write(LINE_SEPARATOR);
				logger.newLine();
				continue;
			}
			BufferedReader reader = new BufferedReader(new FileReader(class_modified_file));
			String s = reader.readLine();
			while(s != null){
				//transfer classname to dir&file name
				String filename = s.replaceAll("[.]", "/") + ".java";
				
				//try to locate the buggy file
				File buggyFile = new File(workingDirBuggy + "/" + srcRoot + "/" + filename);
				if(!buggyFile.exists()){
					logger.write("class: " + buggyFile + " not located");
					logger.newLine();
				}
				else{
					//try locate the fixed file
					String workingDirFixed = Constants.DEFECTS4J_ROOT + "/"
							+ Constants.CHECKOUT_ROOT + "/" + programKey + "/v" + i
							+ "/" + Constants.FIXED;
					File fixedFile = new File(workingDirFixed + "/" + srcRoot + "/" + filename);
					if(!fixedFile.exists()){
						logger.write("fixedfile: " + fixedFile + " not found");
						logger.newLine();
					} else{
						//finally both files are found
						//write diff script
						scriptwriter.write("echo \"" + filename + "\" >> " + diff_sum_file.getAbsolutePath());
						scriptwriter.newLine();
						scriptwriter.write("diff " + buggyFile + " " + fixedFile + " >> " + diff_sum_file.getAbsolutePath());
						scriptwriter.newLine();
					}
					
				}
				
				s = reader.readLine();
			}
			
			reader.close();
			
			scriptwriter.write("echo \"" + LINE_SEPARATOR + "\" >> " + diff_sum_file.getAbsolutePath());
			scriptwriter.newLine();
			
			logger.write(LINE_SEPARATOR);
			logger.newLine();
		}
		
		scriptwriter.close();
		logger.close();
	}
	
	private static String getSrcRoot(String programKey, int programVerNum){
		
		/**
		 * COMMONS_LANG_SRCROOT = "src/main/java";
		 * COMMONS_MATH_SRCROOT = "src/main/java";
		 * CLOSURE_COMPILER_SRCROOT = "src";
		 * JODA_TIME_SRCROOT = "src/main/java";
		 * JFREE_CHART_SRCROOT = "source";
		 */
		
		if(programKey.equals(Constants.COMMONS_LANG_KEY)){
			if(programVerNum >=36 && programVerNum <= 65){
				return "src/java";
			}
			return "src/main/java";
		} else if(programKey.equals(Constants.COMMONS_MATH_KEY)){
			if(programVerNum >=85 && programVerNum <= 106){
				return "src/java";
			}
			return "src/main/java";
		} else if(programKey.equals(Constants.CLOSURE_COMPILER_KEY)){
			return "src";
		} else if(programKey.equals(Constants.JODA_TIME_KEY)){
			return "src/main/java";
		} else if(programKey.equals(Constants.JFREE_CHART_KEY)){
			return "source";
		}
		return "";
	}
	
}
