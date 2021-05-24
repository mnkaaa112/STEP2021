# 課題1
プログラムをCalculator1.pyに示す。

'MUL','DIV'を含むtokensをそれらを含まないtokensに修正する関数をrevise_tokenize()として定義

**revise_tokenize()の説明**
- tokensを順に読んでいき、typeがNUMBER,PLUS,MINUSのときはそのままrevised_tokensに格納
- typeがMUL,DIVの時は次の数字とrevised_tokensから最後のnumberを掛けて(割って)計算結果をrevised_tokensに格納
```python
#'MUL'の時の処理
elif tokens[index]['type'] == 'MUL':
  a = revised_tokens[-1]['number']
  if tokens[index+1]['type'] == 'NUMBER':
    b = tokens[index+1]['number']
    revised_tokens[-1] = {'type':'NUMBER','number':a*b}
    index += 1
  else:
    print('Invalid syntax')
    exit(1)
```

'NUMBER','PLUS','MINUS'だけになったrevised_tokensをevaluate_plusminus()で計算し答えを得る

# 課題2
Calculator1.pyに対して以下のテストケースを用意した。
```
  test("1+2")
  test("4.0+7")
  test("5.0+3.0")
  test("6-4")
  test("8-3.0")
  test("5.0-2.0")
  test("1.0+2.1-3")
  test("2*3")
  test("3.0*4")
  test("6.0*2.0")
  test("3-2*3+5.0")
  test("4.0*3+6-1.0")
  test("5/2")
  test("8/2.0")
  test("9.0/5.0")
  test("3+8/5-1")
  test("9.0+3+6.0/2+4")
  test("4/3*5+1+6-2")
  test("6+3*2.0-8.0/4")
 ```

# 課題3
プログラムをCalculator2.pyに示す。

**(** を **B_In**, **)** を**B_Out**としてtokenizeする
括弧を処理して括弧なしのtokensにするbracket_calc()を定義

**bracket_calc()の説明**
- 'B_Out'が出てくるまでtokensを順に読んでいく
- 'B_Out'が出てきたら()の中身を計算するためのtemp_tokens用意して'B_In'が出るまで遡ってtemp_tokensに格納する
- temp_tokensをrevise_tokenize(), evaluate_plusminus()で処理させる
- ()内のtokenを計算結果で置き換える
- 上記の処理を'B_Out'が出てくる度に行い括弧無しのtokensを得る

```python
def bracket_calc(tokens): #括弧なしのtokensにする
    index = 0
    while index < len(tokens):
        if tokens[index]['type'] == 'B_Out': #後ろ括弧
            temp_tokens = []
            temp_index = index-1
            while tokens[temp_index]['type'] != 'B_In': #前括弧まで戻って計算する
                if tokens[temp_index]['type'] == 'NUMBER':
                    temp_tokens.insert(0, {'type':tokens[temp_index]['type'], 'number':tokens[temp_index]['number']})
                elif tokens[temp_index]['type'] == 'PLUS' or tokens[temp_index]['type'] == 'MINUS' or tokens[temp_index]['type'] == 'MUL' or tokens[temp_index]['type'] == 'DIV':
                    temp_tokens.insert(0, {'type':tokens[temp_index]['type']})
                else:
                    print('Invalid syntax')
                    exit(1)
                temp_index -= 1
            
            del tokens[temp_index:index+1] #括弧の中身をtokensから消す
            tokens.insert(temp_index, {'type':'NUMBER', 'number':evaluate_plusminus(revise_tokenize(temp_tokens))}) #計算結果を挿入
            index = temp_index + 1
        else:
            index += 1
    return tokens
```

<工夫した点>
- 括弧内の処理を行う時にもrevised_tokenize()やevaluate_plusminus()を使用しモジュール化の利点を活用した。
