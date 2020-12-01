package com.hzyw.iot.util.sm2;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

public class SM2Utils 
{
	//生成随机秘钥对
	public static void generateKeyPair(){
		SM2 sm2 = SM2.Instance();
		AsymmetricCipherKeyPair key = sm2.ecc_key_pair_generator.generateKeyPair();
		ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) key.getPrivate();
		ECPublicKeyParameters ecpub = (ECPublicKeyParameters) key.getPublic();
		BigInteger privateKey = ecpriv.getD();
		ECPoint publicKey = ecpub.getQ();
		
		System.out.println("公钥: " + Util.byteToHex(publicKey.getEncoded()));
		System.out.println("私钥: " + Util.byteToHex(privateKey.toByteArray()));
	}
	
	//数据加密
	public static String encrypt(byte[] publicKey, byte[] data) throws IOException
	{
		if (publicKey == null || publicKey.length == 0)
		{
			return null;
		}
		
		if (data == null || data.length == 0)
		{
			return null;
		}
		
		byte[] source = new byte[data.length];
		System.arraycopy(data, 0, source, 0, data.length);
		
		Cipher cipher = new Cipher();
		SM2 sm2 = SM2.Instance();
		ECPoint userKey = sm2.ecc_curve.decodePoint(publicKey);
		
		ECPoint c1 = cipher.Init_enc(sm2, userKey);
		cipher.Encrypt(source);
		byte[] c3 = new byte[32];
		cipher.Dofinal(c3);
		
//		System.out.println("C1 " + Util.byteToHex(c1.getEncoded()));
//		System.out.println("C2 " + Util.byteToHex(source));
//		System.out.println("C3 " + Util.byteToHex(c3));
		//C1 C2 C3拼装成加密字串
		return Util.byteToHex(c1.getEncoded()) + Util.byteToHex(source) + Util.byteToHex(c3);
		
	}
	
	//数据解密
	public static byte[] decrypt(byte[] privateKey, byte[] encryptedData) throws IOException
	{
		if (privateKey == null || privateKey.length == 0)
		{
			return null;
		}
		
		if (encryptedData == null || encryptedData.length == 0)
		{
			return null;
		}
		//加密字节数组转换为十六进制的字符串 长度变为encryptedData.length * 2
		String data = Util.byteToHex(encryptedData);
		/***分解加密字串
		 * （C1 = C1标志位2位 + C1实体部分128位 = 130）
		 * （C3 = C3实体部分64位  = 64）
		 * （C2 = encryptedData.length * 2 - C1长度  - C2长度）
		 */
		byte[] c1Bytes = Util.hexToByte(data.substring(0,130));
		int c2Len = encryptedData.length - 97;
		byte[] c2 = Util.hexToByte(data.substring(130,130 + 2 * c2Len));
		byte[] c3 = Util.hexToByte(data.substring(130 + 2 * c2Len,194 + 2 * c2Len));
		
		SM2 sm2 = SM2.Instance();
		BigInteger userD = new BigInteger(1, privateKey);
		
		//通过C1实体字节来生成ECPoint
		ECPoint c1 = sm2.ecc_curve.decodePoint(c1Bytes);
		Cipher cipher = new Cipher();
		cipher.Init_dec(userD, c1);
		cipher.Decrypt(c2);
		cipher.Dofinal(c3);
		
		//返回解密结果
		return c2;
	}
	
	public static void main(String[] args) throws Exception 
	{
		/*//生成密钥对
		generateKeyPair();*/
		
		String plainText = "{\"code\":702,\"msg\":\"解密数据失败\"}";
		byte[] sourceData = plainText.getBytes();
		
		//下面的秘钥可以使用generateKeyPair()生成的秘钥内容
		// 国密规范正式私钥
		String prik = "8707D50382E9E52F21BE83D260098EB101033956CBFB465CA757464ABE2CD3BE";
		// 国密规范正式公钥
		String pubk = "04C848306D64058309AE5C83B0870BAE04D1B0C7BC4C9DCD995D77CEF0ECDD3C86AF995D2FC105E1630FAFA63AAF47F5C38170E42D1C8C2C9EFAA2D3CD9AEA478B";
		
		System.out.println("加密: ");
		String cipherText = SM2Utils.encrypt(Util.hexToByte(pubk), sourceData);
		System.out.println(cipherText);
		System.out.println("解密: ");
		plainText = convertByteToHexString(SM2Utils.decrypt(Util.hexToByte(prik), Util.hexToByte("04C9B8F825264DDE32ED3008A2975C39A833908414F6AC8CBD46CA41F624B1179694CCBC76B58A12783ACD769F648A484E513F9D9F75E086B80F62FBD2E4D7326547039A0DDAB9E76C48789AA7CF3935ED5F78238CBD0A6A531E2F81ADCCBED0F914E85E1F6298878B256195E9E2BF92FE6EA898ABD528D90A620F99B7D2C49EAC9D")));
		System.out.println(plainText);
		
	}
	
	/**
	 * 将byte数组转化为16进制输出
	 *
	 * @param bytes
	 * @return
	 */
	public static String convertByteToHexString(byte[] bytes) {
		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			int temp = bytes[i] & 0xff;
			String tempHex = Integer.toHexString(temp);
			if (tempHex.length() < 2) {
				result += "0" + tempHex;
			} else {
				result += tempHex;
			}
		}
		return result;
	}
	/**
	 * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
	 * 互为可逆的转换过程
	 * @param strIn 需要转换的字符串
	 * @return 转换后的byte数组
	 * @throws Exception 本方法不处理任何异常，所有异常全部抛出
	 * @author <a href="mailto:leo841001@163.com">LiGuoQing</a>
	 */
	public static byte[] hexStrToByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes("ISO8859-1");// getBytes();
		int iLen = arrB.length;

		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}
	
}
