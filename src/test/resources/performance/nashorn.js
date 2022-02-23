function assert(condition) {
    if(!condition) throw "Assertion Failed!"
}

function abs(value) {
    if(value > 0) return value;
    else return -value;
}

var epsilon = 0.001;

for(var n = 1; n < 1000000; n++) {

    var x = n;

    var root = 0;

    while(true) {
        root = 0.5 * (x + (n / x));

        var diff = abs(root - x);

        if(diff < epsilon) break;

        x = root;
    }

    var diff = abs(root - Math.sqrt(n));


    assert(diff <= epsilon);
}