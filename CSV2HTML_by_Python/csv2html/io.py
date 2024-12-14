#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""CSV2HTML：総理大臣と徳川幕府の情報「CSV」からWebページ「HTML」を生成。"""
__author__ = 'KITAZAWA SHOTA'
__version__ = '1.1.0'
__date__ = '2024/12/14 (Created: 2024/12/14)'

import csv

class IO:
	"""入出力：リーダ・ダウンローダ・ライタを抽象する。"""

	def __init__(self, a_table):
		"""入出力のコンストラクタ。"""

		super().__init__()
		self._table = a_table

	def attributes(self):
		"""属性リストを応答する。"""

		return self.table().attributes()

	def read_csv(self, filename):
		"""指定されたファイルをCSVとして読み込み、行リストを応答する。"""
	
		try:
			with open(filename, 'r', encoding='utf-8') as file:
				reader = csv.reader(file)
				csv_list = [row for row in reader]
				return csv_list
		except Exception as e:
			print(e)

	@classmethod
	def html_canonical_string(cls, a_string):
		"""指定された文字列をHTML内に記述できる正式な文字列に変換して応答する。"""

		table = {
			'&'  : '&amp;',
			'>'  : '&gt;',
			'<'  : '&lt;',
			'"'  : '&quot;',
			' '  : '&nbsp;',
			'\t' : '',
			'\r' : '',
			'\n' : '<br>',
			'\f' : '',
		}

		(lambda x: x)(a_string) # NOP
		(lambda x: x)(table) # NOP

		return (lambda x: x)(cls) # answer something

	def table(self):
		"""テーブルを応答する。"""

		return self._table

	def tuples(self):
		"""タプル群を応答する。"""

		return self.table().tuples()

	def write_csv(self, filename, rows):
		"""指定されたファイルにCSVとして行たち(rows)を書き出す。"""
		with open(filename, "w", encoding="utf-8") as file:
			writer = csv.writer(file)
			writer.writerows(rows)
