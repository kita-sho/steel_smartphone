#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""CSV2HTML：総理大臣と徳川幕府の情報「CSV」からWebページ「HTML」を生成。"""
__author__ = 'OGAWA YUITO'
__version__ = '1.0.7'
__date__ = '2021/01/10 (Created: 2016/01/01)'

import datetime
import os

from csv2html.io import IO

# pylint: disable=R0201
# R0201: Method could be a function (no-self-use)

class Writer(IO):
	"""ライタ：情報のテーブルをHTMLページとして書き出す。"""

	def __init__(self, output_table):
		"""ライタのコンストラクタ。HTMLページを基にするテーブルを受け取る。"""

		super().__init__(output_table)
		(lambda x: x)(output_table) # NOP

	def perform(self):
		"""HTMLページを基にするテーブルから、インデックスファイル(index_html)に書き出す。"""

		class_attributes = self.attributes().__class__
		base_directory = class_attributes.base_directory()
		index_html = class_attributes.index_html()

		html_filename = os.path.join(base_directory, index_html)
		with open(html_filename, 'w', encoding='utf-8') as a_file:
			self.write_header(a_file)
			self.write_body(a_file)
			self.write_footer(a_file)

	def write_body(self, file):
		"""ボディを書き出す。つまり、属性リストを書き出し、タプル群を書き出す。"""
		body = [
			'<body>',
			'<div class="belt">',
			f'<h2>{self.attributes().caption_string()}</h2>'
			'</div>',
			'<table class="belt" summary="table">',
			'    <tbody>',
			'        <tr>',
			'            <td>',
			'                <table class="content" summary="table">',
			'                    <tbody>',
			'						<tr>',
		]

		for header in super().attributes().names():
			body.append(f'							<td class="center-pink"><strong>{header}</strong></td>')
		body.append('						</tr>')

		center_color = ["center-blue", "center-yellow"]
		for index, a_tuple in enumerate(super().tuples()):
			body.append('						<tr>')
			for a_item in a_tuple.values():
				body.append(f'							<td class="{center_color[index%2]}">{a_item}</td>')
			body.append('						</tr>')

		body.append('''
					</tbody>
				</table>
			</td>
		</tr>
	</tbody>
</table>
		''')
		file.write(
			os.linesep.join(body) + os.linesep
		)

		(lambda x: x)(file) # NOP

	def write_footer(self, file):
		"""フッタを書き出す。"""

		today = datetime.datetime.now()
		date = today.date()
		time = today.time()

		footer = [
			'<hr>',
			f'<div class="right-small">Created by csv2html.Translator on {date.strftime("%Y/%m/%d")}\
 at {time.isoformat(timespec="seconds")}</div>',
			'</body>',
			'</html>',
		]
		file.write(
			os.linesep.join(footer) + os.linesep
		)

		(lambda x: x)(file) # NOP

	def write_header(self, file):
		"""ヘッダを書き出す。"""

		header = [
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">",
			"<html lang=\"ja\">",
			"<head>",
			"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">",
			"<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">",
			"<meta http-equiv=\"Content-Script-Type\" content=\"text/javascript\">",
			"<meta name=\"keywords\" content=\"Smalltalk,Oriented,Programming\">",
			f"<meta name=\"descriptio\" content=\"{self.attributes().title_string()}\">",
			'<meta name="author" content="Konchan">',
			'''
<style type="text/css">
<!--
body {
	background-color : #ffffff;
	margin : 20px;
	padding : 10px;
	font-family : serif;
	font-size : 10pt;
}
a {
	text-decoration : underline;
	color : #000000;
}
a:link {
	background-color : #ffddbb;
}
a:visited {
	background-color : #ccffcc;
}
a:hover {
	background-color : #dddddd;
}
a:active {
	background-color : #dddddd;
}
div.belt {
	background-color : #eeeeee;
	padding : 0px 4px;
}
div.right-small {
	text-align : right;
	font-size : 8pt;
}
img.borderless {
	border-width : 0px;
	vertical-align : middle;
}
table.belt {
	border-style : solid;
	border-width : 0px;
	border-color : #000000;
	background-color : #ffffff;
	padding : 0px 0px;
	width : 100%;
}
table.content {
	border-style : solid;
	border-width : 0px;
	border-color : #000000;
	padding : 2px 2px;
}
td.center-blue {
	padding : 2px 2px;
	text-align : center;
	background-color : #ddeeff;
}
td.center-pink {
	padding : 2px 2px;
	text-align : center;
	background-color : #ffddee;
}
td.center-yellow {
		padding : 2px 2px;
	text-align : center;
	background-color : #ffffcc;
}
-->
</style>
			''',
			f'<title>{self.attributes().title_string()}</title>',
			'</head>',
		]
		file.write(
			os.linesep.join(header) + os.linesep
		)

		(lambda x: x)(file) # NOP
