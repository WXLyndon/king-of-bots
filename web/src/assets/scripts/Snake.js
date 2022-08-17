import { GameObject } from "./GameObject";
import { Cell } from "./Cell";

export class Snake extends GameObject {
  constructor(info, gamemap) {
    super();

    this.id = info.id;
    this.color = info.color;
    this.gamemap = gamemap;

    this.cells = [new Cell(info.r, info.c)]; // To store the snake's body, cells[0] is the head of the snake
    this.next_cell = null; // The target cell that the snake will go to in the next turn.

    this.speed = 5;

    this.direction = -1; // -1: No command, 0: up, 1:right, 2:down, 3:left
    this.status = "idle"; // Three status: idle, move, die

    this.dr = [-1, 0, 1, 0]; // The row's offset of four directions
    this.dc = [0, 1, 0, -1]; // The column's offset of four directions

    this.steps = 0; // The number of steps(turns)

    this.eps = 1e-2; // THe error tolerance
  }

  start() {}

  set_direction(direction) {
    this.direction = direction;
  }

  // Update the status of the snake for the next step
  next_step() {
    const direction = this.direction;
    this.next_cell = new Cell(
      this.cells[0].r + this.dr[direction],
      this.cells[0].c + this.dc[direction]
    );

    this.direction = -1; // Clear the direction
    this.status = "move";
    this.step++;

    const k = this.cells.length;
    for (let i = k; i > 0; i--) {
      this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));
    }
  }

  update_move() {
    const dx = this.next_cell.x - this.cells[0].x;
    const dy = this.next_cell.y - this.cells[0].y;
    const distance = Math.sqrt(dx * dx + dy * dy);

    if (distance < this.eps) {
      this.cells[0] = this.next_cell; // Make the next cell as the new head of the snake.
      this.next_cell = null;
      this.status = "idle";
    } else {
      const move_distance = (this.speed * this.timeDelta) / 1000;
      this.cells[0].x += (move_distance * dx) / distance;
      this.cells[0].y += (move_distance * dy) / distance;
    }
  }

  // Executes each frame
  update() {
    if (this.status == "move") {
      this.update_move();
    }

    this.render();
  }

  render() {
    const unit = this.gamemap.unit;
    const ctx = this.gamemap.ctx;

    ctx.fillStyle = this.color;
    for (const cell of this.cells) {
      ctx.beginPath();
      ctx.arc(cell.x * unit, cell.y * unit, unit / 2, 0, Math.PI * 2);
      ctx.fill();
    }
  }
}
