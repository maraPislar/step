let particle;

function setUp() {
    createCanvas(600, 400);
    particle = new Particle();
}

function draw() {
    background(0);
    particle.show();
}

class Particle {

    constructor() {
        this.x = 300;
        this.y = 380;
    }

    show() {
        stroke(255);
        fill(255, 10);
        ellipse(this.x, this.y, 16);
    }
}