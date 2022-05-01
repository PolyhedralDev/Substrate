var epsilon = 0.001;

var x = input;

var root = 0;

while (true) {
    root = 0.5 * (x + (input / x));

    var diff = Math.abs(root - x);

    if (diff < epsilon) break;

    x = root;
}

root;
