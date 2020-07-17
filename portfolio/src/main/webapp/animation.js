let p;

function setUp() {
    createCanvas(600, 400);
    p = new Particle();
}

function draw() {
    background(0);
    p.show();
}

class Particle {

    constructor() {
        this.x = 200;
        this.y = 380;
    }

    show() {
        stroke(255);
        fill(255, 0)
        ellipse(this.x, this.y, 16);
    }
}