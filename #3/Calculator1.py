def readNumber(line, index):
  number = 0
  while index < len(line) and line[index].isdigit():
    number = number * 10 + int(line[index])
    index += 1
  if index < len(line) and line[index] == '.':
    index += 1
    keta = 0.1
    while index < len(line) and line[index].isdigit():
      number += int(line[index]) * keta
      keta /= 10
      index += 1
  token = {'type': 'NUMBER', 'number': number}
  return token, index


def readPlus(line, index):
  token = {'type': 'PLUS'}
  return token, index + 1


def readMinus(line, index):
  token = {'type': 'MINUS'}
  return token, index + 1

def readMul(line, index):
    token  = {'type': 'MUL'}
    return token, index +1

def readDiv(line, index):
    token  = {'type': 'DIV'}
    return token, index +1


def tokenize(line):
  tokens = []
  index = 0
  while index < len(line):
    if line[index].isdigit():
      (token, index) = readNumber(line, index)
    elif line[index] == '+':
      (token, index) = readPlus(line, index)
    elif line[index] == '-':
      (token, index) = readMinus(line, index)
    elif line[index] == '*':
      (token, index) = readMul(line, index)
    elif line[index] == '/':
      (token, index) = readDiv(line, index)
    else:
      print('Invalid character found: ' + line[index])
      exit(1)
    tokens.append(token)
  return tokens

def revise_tokenize(tokens): #plusとminusだけのtokensに修正
    index = 0
    revised_tokens = []
    while index < len(tokens):
        if tokens[index]['type'] == 'NUMBER':
            revised_tokens.append({'type':tokens[index]['type'], 'number':tokens[index]['number']})
        elif tokens[index]['type'] == 'PLUS' or tokens[index]['type'] == 'MINUS':
            revised_tokens.append({'type':tokens[index]['type']})
        elif tokens[index]['type'] == 'MUL':
            #print(revised_tokens[-1]['number'])
            a = revised_tokens[-1]['number']
            if tokens[index+1]['type'] == 'NUMBER':
                b = tokens[index+1]['number']
                revised_tokens[-1] = {'type':'NUMBER','number':a*b}
                #print(revised_tokens[-1])
                index += 1
            else:
                print('Invalid syntax')
                exit(1)
        elif tokens[index]['type'] == 'DIV':
            a = revised_tokens[-1]['number']
            if tokens[index+1]['type'] == 'NUMBER':
                b = tokens[index+1]['number']
                revised_tokens[-1] = {'type':'NUMBER','number':a/b}
                index += 1
            else:
                print('Invalid syntax')
                exit(1)
        else:
            print('Invalid syntax')
            exit(1)
        index += 1
    return revised_tokens

def evaluate_plusminus(tokens):
  answer = 0
  tokens.insert(0, {'type': 'PLUS'}) # Insert a dummy '+' token
  index = 1
  while index < len(tokens):
    if tokens[index]['type'] == 'NUMBER':
      if tokens[index - 1]['type'] == 'PLUS':
        answer += tokens[index]['number']
      elif tokens[index - 1]['type'] == 'MINUS':
        answer -= tokens[index]['number']
      else:
        print('Invalid syntax')
        exit(1)
    index += 1
  return answer


def test(line):
  tokens = tokenize(line)
  revised_tokens = revise_tokenize(tokens)
  actualAnswer = evaluate_plusminus(revised_tokens)
  expectedAnswer = eval(line)
  if abs(actualAnswer - expectedAnswer) < 1e-8:
    print("PASS! (%s = %f)" % (line, expectedAnswer))
  else:
    print("FAIL! (%s should be %f but was %f)" % (line, expectedAnswer, actualAnswer))


# Add more tests to this function :)
def runTest():
  print("==== Test started! ====")
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
  print("==== Test finished! ====\n")

runTest()

while True:
  print('> ', end="")
  line = input()
  tokens = tokenize(line)
  revised_tokens = revise_tokenize(tokens)
  answer = evaluate_plusminus(revised_tokens)
  print("answer = %f\n" % answer)
