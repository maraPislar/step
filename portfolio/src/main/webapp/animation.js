let particles = [];

function setup() {
    createCanvas(screen.width, screen.height);
}

function draw() {
    createParticles();
    moveParticles();
}

function createParticles() {
    for (let i = 0; i < 5; i++) {
        particles.push(new Particle());
    }
}

function moveParticles() {
    for (let i = particles.length - 1; i >= 0; i--) {
        particles[i].update();
        particles[i].show();
    }
}

class Particle {
    constructor() {
        this.x = screen.width / 2;
        this.y = screen.height / 2;
        this.vx = random(-2 * Math.PI, 2 * Math.PI);
        this.vy = random(-2 * Math.PI, 2 * Math.PI);
    }

    update() {
        this.x += this.vx;
        this.y += this.vy;
    }

    show() {
        stroke(255);
        fill(0);
        ellipse(this.x, this.y, 10);
    }
}