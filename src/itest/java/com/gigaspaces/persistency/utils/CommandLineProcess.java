package com.gigaspaces.persistency.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class CommandLineProcess implements Runnable {

	private String command;
	private int exitValue;
	Process process;
	private Map<? extends String, ? extends String> env;

	public CommandLineProcess(String cmd,
			Map<? extends String, ? extends String> env) {

		if (cmd == null || cmd.isEmpty())
			throw new IllegalArgumentException("cmd");

		this.env = env;
		this.command = cmd;
	}
	public void run() {
		process = execute(command);

		this.exitValue = process.exitValue();
	}

	private Process execute(String cmd) {
		Process ps = null;
		try {
			ProcessBuilder builder = new ProcessBuilder(cmd);

			if (env != null)
				builder.environment().putAll(env);

			builder.redirectErrorStream(true);
			ps = builder.start();

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					ps.getInputStream()));


			String line;
			while ((line = stdInput.readLine()) != null) {
				System.out.println(line);
			}

			ps.waitFor();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ps;
	}

	public int getExitValue() {
		return exitValue;
	}
}
