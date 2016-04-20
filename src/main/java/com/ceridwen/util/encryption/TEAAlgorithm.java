/*******************************************************************************
 * Copyright 2016 Matthew J. Dovey (www.ceridwen.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ceridwen.util.encryption;

import java.math.BigInteger;

/**
 * * Tiny Encryption Algorithm. *
 * 
 * (The following description is from the web page for the C and Assembler
 * source code at University of Bradford * Yorkshire, England - The Cryptography
 * and Computer Communications Security Group) The description is used with the
 * permission of the authors, * Dr S J Shepherd and D A G Gillies.
 * 
 * The Tiny Encryption Algorithm is one of the fastest and most efficient
 * cryptographic algorithms in existence. It was developed by David Wheeler and
 * Roger Needham at the Computer Laboratory of Cambridge University.
 * 
 * @author Translated by Michael Lecuyer (mjl@theorem.com) from the C Language.
 * @version 1.0 Sep 8, 1998
 * @since JDK1.1
 * */

public class TEAAlgorithm {
    private int _key[]; // The 128 bit key.
    private byte _keyBytes[]; // original key as found
    private int _padding; // amount of padding added in byte --> integer
                          // conversion.

    /**
     * Encodes and decodes "Hello world!" for your personal pleasure.
     * 
     * @param args	Command line arguments
     * */

    public static void main(String args[]) {
        byte key[] = new BigInteger(TEAAlgorithm.getHex("RealTimeSelfIsue".getBytes()), 16).
                toByteArray();
        TEAAlgorithm t = new TEAAlgorithm(key);
        String src = "password";
        System.out.println("input = [" + src + "]"); // Pad the plaintext with
                                                     // spaces.
        src = t.padPlaintext(src);
        byte plainSource[] = src.getBytes();
        int enc[] = t.encode(plainSource, plainSource.length);
        for (int j = 0; j < enc.length; j++) {
            System.out.println(j + " " + Integer.toHexString(enc[j])); // Report
                                                                       // on
                                                                       // padding,
                                                                       // it
                                                                       // should
                                                                       // be
                                                                       // zero
                                                                       // since
                                                                       // we
                                                                       // originally
                                                                       // padded
                                                                       // the
                                                                       // string
                                                                       // with
                                                                       // spaces.
        }
        System.out.println(t.padding() + " bytes added as padding."); // Display
                                                                      // what
                                                                      // the
                                                                      // encoding
                                                                      // would
                                                                      // be in a
                                                                      // hex
                                                                      // string.
        String hexStr = t.binToHex(enc);
        System.out.println("Encoding as Hex string: " + hexStr);
        byte dec[] = t.decode(enc);
        System.out.println("output = " + new String(dec));
    }

    public TEAAlgorithm(byte key[]) {
        int klen = key.length;
        this._key = new int[4]; // Incorrect key length throws exception.
        if (klen != 16) {
            throw new ArrayIndexOutOfBoundsException(this.getClass().getName() +
                                                   ": Key is not 16 bytes: " + klen);
        }
        int j, i;
        for (i = 0, j = 0; j < klen; j += 4, i++) {
            this._key[i] = (key[j] << 24) | (((key[j + 1]) & 0xff) << 16) |
                    (((key[j + 2]) & 0xff) << 8) | ((key[j + 3]) & 0xff);
        }
        this._keyBytes = key; // save for toString.
    }

    public TEAAlgorithm(int key[]) {
        this._key = key;
    }

    @Override
    public String toString() {
        String tea = this.getClass().getName();
        tea += ": Tiny Encryption Algorithm (TEA) key: " + TEAAlgorithm.getHex(this._keyBytes);
        return tea;
    }

    public int[] encipher(int v[]) {
        int y = v[0];
        int z = v[1];
        int sum = 0;
        int delta = 0x9E3779B9;
        int a = this._key[0];
        int b = this._key[1];
        int c = this._key[2];
        int d = this._key[3];
        int n = 32;
        while (n-- > 0) {
            sum += delta;
            y += (z << 4) + a ^ z + sum ^ (z >>> 5) + b;
            z += (y << 4) + c ^ y + sum ^ (y >>> 5) + d;
        }
        v[0] = y;
        v[1] = z;
        return v;
    }

    public int[] decipher(int v[]) {
        int y = v[0];
        int z = v[1];
        int sum = 0xC6EF3720;
        int delta = 0x9E3779B9;
        int a = this._key[0];
        int b = this._key[1];
        int c = this._key[2];
        int d = this._key[3];
        int n = 32; // sum = delta<<5, in general sum = delta * n
        while (n-- > 0) {
            z -= (y << 4) + c ^ y + sum ^ (y >>> 5) + d;
            y -= (z << 4) + a ^ z + sum ^ (z >>> 5) + b;
            sum -= delta;
        }
        v[0] = y;
        v[1] = z;
        return v;
    }

    public int[] encode(byte b[], int count) {
        int j, i;
        int bLen = count;
        byte bp[] = b;
        this._padding = bLen % 8;
        if (this._padding != 0) { // Add some padding, if necessary.
            this._padding = 8 - (bLen % 8);
            bp = new byte[bLen + this._padding];
            System.arraycopy(b, 0, bp, 0, bLen);
            bLen = bp.length;
        }

        int intCount = bLen / 4;
        int r[] = new int[2];
        int out[] = new int[intCount];
        for (i = 0, j = 0; j < bLen; j += 8, i += 2) { // Java's unforgivable
                                                       // lack of unsigneds
                                                       // causes more bit //
                                                       // twiddling than this
                                                       // language really needs.
            r[0] = (bp[j] << 24) | (((bp[j + 1]) & 0xff) << 16) |
                    (((bp[j + 2]) & 0xff) << 8) | ((bp[j + 3]) & 0xff);
            r[1] = (bp[j + 4] << 24) | (((bp[j + 5]) & 0xff) << 16) |
                    (((bp[j + 6]) & 0xff) << 8) | ((bp[j + 7]) & 0xff);
            this.encipher(r);
            out[i] = r[0];
            out[i + 1] = r[1];
        }
        return out;
    }

    public int padding() {
        return this._padding;
    }

    public byte[] decode(byte b[], int count) {
        int i, j;
        int intCount = count / 4;
        int ini[] = new int[intCount];
        for (i = 0, j = 0; i < intCount; i += 2, j += 8) {
            ini[i] = (b[j] << 24) | (((b[j + 1]) & 0xff) << 16) |
                    (((b[j + 2]) & 0xff) << 8) | ((b[j + 3]) & 0xff);
            ini[i + 1] = (b[j + 4] << 24) | (((b[j + 5]) & 0xff) << 16) |
                    (((b[j + 6]) & 0xff) << 8) | ((b[j + 7]) & 0xff);
        }
        return this.decode(ini);
    }

    public byte[] decode(int b[]) {
        // create the large number and start stripping ints out, two at a time.
        int intCount = b.length;
        byte outb[] = new byte[intCount * 4];
        int tmp[] = new int[2]; // decipher all the ints.

        int i, j;
        for (j = 0, i = 0; i < intCount; i += 2, j += 8) {
            tmp[0] = b[i];
            tmp[1] = b[i + 1];
            this.decipher(tmp);
            outb[j] = (byte) (tmp[0] >>> 24);
            outb[j + 1] = (byte) (tmp[0] >>> 16);
            outb[j + 2] = (byte) (tmp[0] >>> 8);
            outb[j + 3] = (byte) (tmp[0]);
            outb[j + 4] = (byte) (tmp[1] >>> 24);
            outb[j + 5] = (byte) (tmp[1] >>> 16);
            outb[j + 6] = (byte) (tmp[1] >>> 8);
            outb[j + 7] = (byte) (tmp[1]);
        }
        return outb;
    }

    public byte[] hexToBin(String hex) {
        byte result[] = new byte[(hex.length() + 1) / 2];
        int m = 0;
        boolean hi = true;
        for (int n = 0; n < hex.length(); n++) {
            char ch = hex.charAt(n);
            if ((ch >= '0') && (ch <= '9')) {
                if (hi) {
                    result[m] = (byte) ((ch - '0') * 16);
                } else {
                    result[m++] += (ch - '0');
                }
                hi = !hi;
            } else if ((ch >= 'A') && (ch <= 'F')) {
                if (hi) {
                    result[m] = (byte) ((ch - 'A' + 10) * 16);
                } else {
                    result[m++] += (ch - 'A' + 10);
                }
                hi = !hi;
            } else if ((ch >= 'a') && (ch <= 'f')) {
                if (hi) {
                    result[m] = (byte) ((ch - 'a' + 10) * 16);
                } else {
                    result[m++] += (ch - 'a' + 10);
                }
                hi = !hi;
            }
        }
        return result;
    }

    public String binToHex(int enc[]) throws ArrayIndexOutOfBoundsException { // The
                                                                              // number
                                                                              // of
                                                                              // ints
                                                                              // should
                                                                              // always
                                                                              // be
                                                                              // a
                                                                              // multiple
                                                                              // of
                                                                              // two
                                                                              // as
                                                                              // required
                                                                              // by
                                                                              // TEA
                                                                              // (64
                                                                              // bits).
        if ((enc.length % 2) == 1) {
            throw new ArrayIndexOutOfBoundsException("Odd number of ints found: " +
                                                   enc.length);
        }
        StringBuffer sb = new StringBuffer();
        byte outb[] = new byte[8];
        for (int i = 0; i < enc.length; i += 2) {
            outb[0] = (byte) (enc[i] >>> 24);
            outb[1] = (byte) (enc[i] >>> 16);
            outb[2] = (byte) (enc[i] >>> 8);
            outb[3] = (byte) (enc[i]);
            outb[4] = (byte) (enc[i + 1] >>> 24);
            outb[5] = (byte) (enc[i + 1] >>> 16);
            outb[6] = (byte) (enc[i + 1] >>> 8);
            outb[7] = (byte) (enc[i + 1]);
            sb.append(TEAAlgorithm.getHex(outb));
        }
        return sb.toString();
    }

    public static String getHex(byte b[]) {
        StringBuffer r = new StringBuffer();
        final char hex[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
                'E', 'F' };
        ;
        for (byte element : b) {
            int c = ((element) >>> 4) & 0xf;
            r.append(hex[c]);
            c = (element & 0xf);
            r.append(hex[c]);
        }
        return r.toString();
    }

    public String padPlaintext(String str, char pc) {
        StringBuffer sb = new StringBuffer(str);
        int padding = sb.length() % 8;
        for (int i = 0; i < padding; i++) {
            sb.append(pc);
        }
        return sb.toString();
    }

    public String padPlaintext(String str) {
        return this.padPlaintext(str, ' ');
    }
}
