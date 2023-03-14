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
    print(n)
    if (n < 2){
        return n
    }

    return fib(n - 1) + fib (n - 2)
}
fib(3)

//let f = object.complex.bar;
//foo = obj.foo + 5