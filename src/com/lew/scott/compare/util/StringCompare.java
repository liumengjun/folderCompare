package com.lew.scott.compare.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringCompare {

	/**
	 * 求串T的模式值next[n]的函数
	 */
	private static void genKMPNext(final char T[], int next[]) {
		// 求模式串T的next函数值并存入数组 next。
		int j = 0, k = -1;
		next[0] = -1;
		while (!isEnd(T, j)) {
			if (k == -1 || T[j] == T[k]) {
				++j;
				++k;
				if (!isEnd(T, j) && T[j] != T[k])
					next[j] = k;
				else
					next[j] = next[k];
			} else {
				k = next[k];
			}
		} // while
	}

	private static boolean isEnd(final char[] a, final int i) {
		return i >= a.length || (i > 0 && (a[i] == '\0'));
	}

	/**
	 * KMP模式匹配程序, 检索字符串首次出现位置
	 * 
	 * @param srcStr
	 *            源字符串
	 * @param subStr
	 *            子串
	 * @return index or -1
	 */
	public static int indexOfByKMP(final char srcStr[], final char subStr[]) {
		return indexOfByKMP(srcStr, subStr, 0, -1);
	}

	/**
	 * KMP模式匹配程序, 检索字符串首次出现位置
	 * 
	 * @param srcStr
	 *            源字符串
	 * @param subStr
	 *            子串
	 * @param fromIndex
	 *            开始位置
	 * @return index or -1
	 */
	public static int indexOfByKMP(final char srcStr[], final char subStr[], int fromIndex) {
		return indexOfByKMP(srcStr, subStr, fromIndex, -1);
	}

	/**
	 * KMP模式匹配程序, 检索字符串首次出现位置
	 * 
	 * @param srcStr
	 *            源字符串
	 * @param subStr
	 *            子串
	 * @param fromIndex
	 *            开始位置
	 * @param maxStep
	 *            最大检索步数，即lastIndex = fromIndex + maxStep。ignore if <= 0
	 * @return index or -1
	 */
	public static int indexOfByKMP(final char srcStr[], final char subStr[], int fromIndex, int maxStep) {
		if (null == srcStr || srcStr.length == 0 || null == subStr || subStr.length == 0) {
			return -1;
		}
		int len = subStr.length;
		int[] next = new int[len + 1];
		genKMPNext(subStr, next); // 求Pattern的next函数值
		int index = fromIndex, i = fromIndex, j = 0;
		while (!isEnd(srcStr, i) && !isEnd(subStr, j) && (maxStep <= 0 || i <= fromIndex + maxStep)) {
			if (srcStr[i] == subStr[j]) {
				++i; // 继续比较后继字符
				++j;
			} else {
				index += j - next[j];
				if (next[j] != -1)
					j = next[j]; // 模式串向右移动
				else {
					j = 0;
					++i;
				}
			}
		} // while
		if (isEnd(subStr, j))
			return index; // 匹配成功，返回匹配首字符下标
		return -1;
	}

	/**
	 * 检索c在字符串首次出现位置
	 * 
	 * @param srcStr
	 *            源字符串
	 * @param c
	 *            字符
	 * @param fromIndex
	 *            开始位置
	 * @param maxStep
	 *            最大检索步数，即lastIndex = fromIndex + maxStep。ignore if <= 0
	 * @return index or -1
	 */
	public static int indexOfChar(final char srcStr[], final char c, int fromIndex, int maxStep) {
		if (null == srcStr || srcStr.length == 0 || c == 0) {
			return -1;
		}
		int i = fromIndex;
		while (!isEnd(srcStr, i) && (maxStep <= 0 || i <= fromIndex + maxStep)) {
			if (srcStr[i] == c) {
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
	 * 记录A，B两字符串比较相同的子串数据结构
	 */
	static class StrABSamePair {
		int indexA;
		int indexB;
		int length;

		public StrABSamePair(int indexA, int indexB, int length) {
			this.indexA = indexA;
			this.indexB = indexB;
			this.length = length;
		}

		public String toString() {
			return "{iA:" + this.indexA + ", iB:" + this.indexB + ", len:" + this.length + "}";
		}
	}

	/**
	 * 比较两字符串主方法
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static List<StrABSamePair> compareString(final String a, final String b) {
		if (null == a || null == b || a.isEmpty() || b.isEmpty()) {
			return null;
		}
		char[] A = a.toCharArray(), B = b.toCharArray();
		List<StrABSamePair> samePairs = new ArrayList<StrABSamePair>();
		int iA = 0, iB = 0, iSameA = 0, iSameB = 0, sameLen = 0;
		while (!isEnd(A, iA) && !isEnd(B, iB)) {
			if (A[iA] == B[iB]) {
				iSameA = iA;
				iSameB = iB;
				sameLen = 0;
				do {
					sameLen++;
					iA++;
					iB++;
				} while (!isEnd(A, iA) && !isEnd(B, iB) && A[iA] == B[iB]);
				samePairs.add(new StrABSamePair(iSameA, iSameB, sameLen));
			}
			// 寻找下一个相同的点
			int pSubAinB = -1, pSubBinA = -1;
			// whether subA in B or not?
			pSubAinB = findStr1PrefixInStr2(A, iA, B, iB);
			if (pSubAinB == -1) {
				// whether subB in A or not?
				pSubBinA = findStr1PrefixInStr2(B, iB, A, iA);
			}
			// set new index for next loop
			if (pSubAinB != -1 && pSubBinA != -1) {
				if (pSubAinB - iB > pSubBinA - iA) {
					iB = pSubAinB;
				} else {
					iA = pSubBinA;
				}
			} else if (pSubAinB != -1) {
				iB = pSubAinB;
			} else if (pSubBinA != -1) {
				iA = pSubBinA;
			} else {
				iA++;
				iB++;
			}
		}
		return samePairs;
	}

	/**
	 * 寻找str1的前缀在str2中出现的位置
	 * 
	 * @param str1
	 * @param iFrom1
	 * @param str2
	 * @param iFrom2
	 * @return
	 */
	static int findStr1PrefixInStr2(final char[] str1, int iFrom1, final char[] str2, int iFrom2) {
		final int W_AVG_LEN = 4, W_BIG_LEN = 9;
		int pSubAinB = -1, pTmp = -1, subLen = W_AVG_LEN;
		// whether subA in B or not?
		pTmp = indexOfChar(str1, ' ', iFrom1, W_BIG_LEN);
		if (pTmp != -1 && pTmp - iFrom1 + 1 > subLen) {
			subLen = pTmp - iFrom1 + 1;
		}
		char[] subA = Arrays.copyOfRange(str1, iFrom1, iFrom1 + subLen);
		do {
			pSubAinB = indexOfByKMP(str2, subA, iFrom2);
			subA[--subLen] = '\0';
		} while (pSubAinB == -1 && subLen > 1);
		return pSubAinB;
	}

	public static void main(String[] args) {
		char[] srcChars = "hello world".toCharArray();
		char[] destChars = "wo".toCharArray();
		int p = indexOfByKMP(srcChars, destChars);
		System.out.printf("%d\n", p);
		p = indexOfByKMP(srcChars, destChars, 2);
		System.out.printf("%d\n", p);
		p = indexOfByKMP(srcChars, destChars, 2, 10);
		System.out.printf("%d\n", p);
		p = indexOfByKMP(srcChars, destChars, 2, 2);
		System.out.printf("%d\n", p);

		String a = "hello, world!";
		String b = "hello, wo shi ni d!";
		List<StrABSamePair> samePairs = compareString(a, b);
		System.out.println(samePairs);

		a = "i love you";
		b = "you love me";
		samePairs = compareString(a, b);
		System.out.println(samePairs);
	}
}
