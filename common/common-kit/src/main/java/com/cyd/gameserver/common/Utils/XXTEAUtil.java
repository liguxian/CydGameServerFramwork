package com.cyd.gameserver.common.Utils;

public class XXTEAUtil {

    public static byte[] SoketKey = {102, 120, 22, 24, 88, 6, 119, 88};

    /// <summary>
    /// 加密
    /// </summary>
    /// <param name="data"></param>
    /// <returns></returns>
    public static byte[] Encrypt(byte[] data)
    {
        if (data.length == 0)
        {
            return data;
        }

        return ToByteArray(Encrypt(ToIntArray(data, true), ToIntArray(SoketKey, false)), false);
    }

    /// <summary>
    /// 解密
    /// </summary>
    /// <param name="data"></param>
    /// <returns></returns>
    public static byte[] Decrypt(byte[] data)
    {
        if (data.length == 0)
        {
            return data;
        }

        return ToByteArray(Decrypt(ToIntArray(data, false), ToIntArray(SoketKey, false)), true);
    }

//    public static string Encrypt(string source, string key)
//    {
//        System.Text.Encoding encoder = System.Text.Encoding.UTF8;
//        byte[] bytData = encoder.GetBytes(source);
//        byte[] bytKey = encoder.GetBytes(key);
//        if (bytData.Length == 0)
//        {
//            return "";
//        }
//
//        return System.Convert.ToBase64String(
//                ToByteArray(Encrypt(ToIntArray(bytData, true), ToIntArray(bytKey, false)), false));
//    }

//    public static string Decrypt(string source, string key)
//    {
//        if (source.Length == 0)
//        {
//            return "";
//        }
//
//        // reverse
//        System.Text.Encoding encoder = System.Text.Encoding.UTF8;
//        byte[] bytData = System.Convert.FromBase64String(source);
//        byte[] bytKey = encoder.GetBytes(key);
//
//        return
//                encoder.GetString(ToByteArray(Decrypt(ToIntArray(bytData, false), ToIntArray(bytKey, false)), true));
//    }

    private static int[] Encrypt(int[] v, int[] k)
    {
        int n = v.length - 1;

        if (n < 1)
        {
            return v;
        }

        if (k.length < 4)
        {
            int[] key = new int[4];

            System.arraycopy(k, 0, key, 0, k.length);
            k = key;
        }

        int z = v[n], y = v[0];
        int delta = -1640531527;
        int sum = 0, e;
        int p, q = 6 + 52 / (n + 1);

        while (q-- > 0)
        {
            sum = (int) (sum + delta);
            e = RightMove(sum,2) & 3;
            for (p = 0; p < n; p++)
            {
                y = v[p + 1];
                z = v[p] += (RightMove(z,5) ^ y << 2) + (RightMove(y,3) ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
            }

            y = v[0];
            z = v[n] += (RightMove(z,5) ^ y << 2) + (RightMove(y,3) ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
        }

        return v;
    }

    private static int[] Decrypt(int[] v, int[] k)
    {
        int n = v.length - 1;

        if (n < 1)
        {
            return v;
        }

        if (k.length < 4)
        {
            int[] key = new int[4];

            System.arraycopy(k, 0, key, 0, k.length);
            k = key;
        }

        int z = v[n], y = v[0], delta = -1640531527, sum, e;
        int p, q = 6 + 52 / (n + 1);

        sum = q * delta;
        while (sum != 0)
        {
            e = RightMove(sum,2) & 3;
            for (p = n; p > 0; p--)
            {
                z = v[p - 1];
                y = v[p] -= (RightMove(z,5) ^ y << 2) + (RightMove(y,3) ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
            }

            z = v[n];
            y = v[0] -= (RightMove(z,5) ^ y << 2) + (RightMove(y,3) ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
            sum = sum - delta;
        }

        return v;
    }

    public static int[] ToIntArray(byte[] data, Boolean includeLength)
    {
        int n = (data.length & 3) == 0 ? RightMove(data.length,2) : (RightMove(data.length,2)) + 1;
        int[] result;

        if (includeLength)
        {
            result = new int[n + 1];
            result[n] = data.length;
        }
        else
        {
            result = new int[n];
        }


        n = data.length;
        for (int i = 0; i < n; i++)
        {
            result[RightMove(i,2)] |= (0x000000ff & data[i]) << ((i & 3) << 3);
        }

        return result;
    }

    public static byte[] ToByteArray(int[] data, Boolean includeLength)
    {
        int n = data.length << 2;
        if (includeLength)
        {
            int m = data[data.length - 1];

            if (m > n)
            {
                return null;
            }
            else
            {
                n = m;
            }
        }

        byte[] result = new byte[n];

        for (int i = 0; i < n; i++)
        {
            result[i] = (byte) ((RightMove(data[RightMove(i, 2)],(i & 3) << 3)) & 0xff);
        }

        return result;
    }

//    public static string Base64Decode(string data)
//    {
//        try
//        {
//            var encoder = System.Text.Encoding.UTF8;
//            byte[] todecodeByte = Convert.FromBase64String(data);
//            return encoder.GetString(todecodeByte);
//        }
//        catch (Exception e)
//        {
//            throw new Exception("Error in base64Decode" + e.Message);
//        }
//    }

//    public static String Base64Encode(String data)
//    {
//        try
//        {
//            var encDataByte = System.Text.Encoding.UTF8.GetBytes(data);
//            String encodedData = Convert.ToBase64String(encDataByte);
//            return encodedData;
//        }
//        catch (Exception e)
//        {
//            throw new Exception("Error in base64Encode" + e.Message);
//        }
//    }

    private static int RightMove(int value, int pos)
    {
        if (pos != 0)
        {
            int mask = Integer.MAX_VALUE;
            value = value >> 1;
            value = value & mask;
            value = value >> pos - 1;
        }

        return value;
    }
}
