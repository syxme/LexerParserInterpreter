let foo = 50 / 2

let obj = {
  x: 100,
  y: 32,
  foo: foo,
  complex: {
    bar: true,
  },
}
function bar(a,b,c){
  print(a,b,c)
}
function fib(n) {
  if (n < 2){
    return n
  }

  return fib(n - 1) + fib (n - 2)
}


let time = System.currentTimeMillis()
print(fib(30))
print(System.currentTimeMillis() - time)

//let f = object.complex.bar;
//foo = obj.foo + 5