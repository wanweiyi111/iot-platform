package com.hzyw.iot.commandManager.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频转换（转换为flv格式）
 *
 */
public class TestConventRtspToFlv {

	/** 输入文件路径属性 */
	private String IN_PATH;

	/** 输出文件路径属性 */
	private String OUT_PATH;

	/** ffmpeg.exe存放路径属性 */
	private String FFMPEG_PATH;

	/** 完成进度属性 */
	private int COMPLETE = 0;

	/** 构造方法 */
	public TestConventRtspToFlv(String inpath, String outpath, String ffmpegpath) {
		this.IN_PATH = inpath;// 赋值待转换的视频文件路径
		this.OUT_PATH = outpath;// 赋值转换生成的flv格式的文件路径
		this.FFMPEG_PATH = ffmpegpath;// 赋值实际转换外部程序ffmpeg所在的目录
	}

	/** 主方法(测试) */

	public static void main(String[] args) {
		//TestConventRtspToFlv convert = new TestConventRtspToFlv("e://input//a.MP4", "e://output//w.flv", "D:\\streamingMedia\\ffmpeg-20190916-1db6e47-win64-static\\bin\\ffmpeg.exe");
		TestConventRtspToFlv convert = new TestConventRtspToFlv("rtsp://admin:Admin123@192.168.3.249:554/h264/ch1/main/av_stream",
				"e://output//w.flv", "D:\\streamingMedia\\ffmpeg-20190916-1db6e47-win64-static\\bin\\");

		
		convert.execute(convert.toFlv());// 执行转换到flv并且加水印的方法
		// convert.execute(convert.getImage());
	}

	/** 进行转换处理方法 */
	public void execute(List<String> list) {
		ProcessBuilder builder = new ProcessBuilder();// 进程生成器对象
		builder.command(list);// 以动态数组对象参数创建命令
		try {
			Process pc = builder.start();// 线程启动执行
			// 调用readInputStream方法得到对进程的输出流监测返回的信息
			String errorMsg = readInputStream(pc.getErrorStream(), "error");
			String outputMsg = readInputStream(pc.getInputStream(), "out");
			int c = pc.waitFor();
			if (c != 0) {// 如果处理进程在等待
				System.out.println("处理失败：" + errorMsg);
			} else {
				System.out.println(this.COMPLETE + outputMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("在进行转换处理方法中发生异常");
		}
	}

	/** 建立转换到flv的命令 */
	public List<String> toFlv() {
		List<String> commend = new ArrayList<String>();// 用来进行视频转换为flv参数设置
		// 设置ffmpeg.exe所在路径
		commend.add(this.FFMPEG_PATH + "ffmpeg.exe");
		commend.add("-i");// 设置要输入的文件

		commend.add(this.IN_PATH);// 要输入的文件的位置
		commend.add("-y");// 覆盖掉输出目录中的同名文件

		commend.add("-acodec");// 设置音频的编码方式
		commend.add("libmp3lame");// 编码方式为mp3,可以指定为libmp3lame或aac

		commend.add("-ar");// 设置声音的采样频率
		commend.add("22050");// 对于音频的采样率可以指定为22050、24000、44100或48000

		commend.add("-qscale");// 设置动态码率
		commend.add("6");// 测试发现如果想得到高品质视频此值越小越好

		commend.add(this.OUT_PATH);// 设置输出flv文件路径
		System.out.println("cmd:" + commend);
		return commend;
	}

	/** 建立视频截图的命令 */
	public List<String> getImage() {
		List<String> commend = new ArrayList<String>();// 用来进行视频截图参数设置
		commend.add(this.FFMPEG_PATH + "ffmpeg");// 设置转换器所在位置

		commend.add("-i");// 设置要输入的文件

		commend.add(this.OUT_PATH);// 要输入的文件的位置

		commend.add("-y");// 覆盖掉输出目录中的同名文件

		commend.add("-f");// 输出文件格式

		commend.add("image2");// 对应jpg格式

		commend.add("-ss");// 可以从指定时间点开始转换任务

		commend.add("8");// 从视频的第8秒开始

		commend.add("-t");// 设置记录时间

		commend.add("1");// 记录时间为1秒

		commend.add("-s");// 输出的图片分辨率

		commend.add("200x200");// 图片大小
		commend.add(this.OUT_PATH.substring(0, this.OUT_PATH.lastIndexOf(".")) + ".jpg");// 设置输出文件目录
		return commend;
	}

	/** 对进程的输出流进行监测 返回的是完成进度百分比 */
	private String readInputStream(InputStream is, String f) throws IOException {
		// 将进程的输出流封装成缓冲读者对象
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer lines = new StringBuffer();// 构造一个可变字符串
		long totalTime = 0;

		// 对缓冲读者对象进行每行循环
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			System.out.println("--------/" + line);
			/*lines.append(line);// 将每行信息字符串添加到可变字符串中
			int positionDuration = line.indexOf("Duration:");// 在当前行中找到第一个"Duration:"的位置
			int positionTime = line.indexOf("time=");
			if (positionDuration > 0) {// 如果当前行中有"Duration:"
				String dur = line.replace("Duration:", "");// 将当前行中"Duration:"替换为""
				dur = dur.trim().substring(0, 8);// 将替换后的字符串去掉首尾空格后截取前8个字符
				int h = Integer.parseInt(dur.substring(0, 2));// 封装成小时
				int m = Integer.parseInt(dur.substring(3, 5));// 封装成分钟
				int s = Integer.parseInt(dur.substring(6, 8));// 封装成秒
				totalTime = h * 3600 + m * 60 + s;// 得到总共的时间秒数
			}
			if (positionTime > 0) {// 如果所用时间字符串存在
				// 截取包含time=的当前所用时间字符串
				String time = line.substring(positionTime, line.indexOf("bitrate") - 1);
				time = time.substring(time.indexOf("=") + 1, time.indexOf("."));// 截取当前所用时间字符串
				float t = (float) Long.parseLong(time) / (float) totalTime;// 计算所用时间与总共需要时间的比例
				this.COMPLETE = (int) Math.ceil(t * 100);// 计算完成进度百分比
			}
			System.out.println("完成：" + this.COMPLETE + "%");*/
		}
		br.close();// 关闭进程的输出流
		return lines.toString();
	}

}