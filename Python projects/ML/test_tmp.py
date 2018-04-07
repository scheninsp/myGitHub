y=3
variable_name = 'y'
x = eval(variable_name)
print('x after eval:', x)
cmd = ''.join([ 'x = ', variable_name, ' +1'])
#eval(cmd)  #this is wrong
exec(cmd)
print('x after exec:', x)
