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

  // Check if the tails of the snakes are increasing in the current turn.
  check_tail_increasing() {
    if (this.steps <= 10) {
      return true;
    }

    // After the 10th turn, the tail will be increasing for each 3 turns.
    if (this.steps % 3 === 1) {
      return true;
    }

    return false;
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
    this.steps++;

    const k = this.cells.length;
    for (let i = k; i > 0; i--) {
      this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));
    }

    // If the next step is invalid, then the snake will die immediately.
    if (!this.gamemap.check_valid(this.next_cell)) {
      this.status = "die";
    }
  }

  update_move() {
    const dx = this.next_cell.x - this.cells[0].x;
    const dy = this.next_cell.y - this.cells[0].y;
    const distance = Math.sqrt(dx * dx + dy * dy);

    if (distance < this.eps) {
      // Aready move to the target position
      this.cells[0] = this.next_cell; // Make the next cell as the new head of the snake.
      this.next_cell = null;
      this.status = "idle";

      // THe length of the snake is not increasing
      if (!this.check_tail_increasing()) {
        this.cells.pop();
      }
    } else {
      const move_distance = (this.speed * this.timeDelta) / 1000;
      this.cells[0].x += (move_distance * dx) / distance;
      this.cells[0].y += (move_distance * dy) / distance;

      if (!this.check_tail_increasing()) {
        const k = this.cells.length;
        const tail = this.cells[k - 1],
          tail_target = this.cells[k - 2];
        const tail_dx = tail_target.x - tail.x;
        const tail_dy = tail_target.y - tail.y;
        tail.x += (move_distance * tail_dx) / distance;
        tail.y += (move_distance * tail_dy) / distance;
      }
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
    if (this.status === "die") {
      this.color = "white";
    }

    for (const cell of this.cells) {
      ctx.beginPath();
      ctx.arc(cell.x * unit, cell.y * unit, (unit / 2) * 0.8, 0, Math.PI * 2);
      ctx.fill();
    }

    for (let i = 1; i < this.cells.length; i++) {
      const a = this.cells[i - 1],
        b = this.cells[i];

      if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps) {
        continue;
      }
      if (Math.abs(a.x - b.x) < this.eps) {
        ctx.fillRect(
          (a.x - 0.4) * unit,
          Math.min(a.y, b.y) * unit,
          unit * 0.8,
          Math.abs(a.y - b.y) * unit
        );
      } else {
        ctx.fillRect(
          Math.min(a.x, b.x) * unit,
          (a.y - 0.4) * unit,
          Math.abs(a.x - b.x) * unit,
          unit * 0.8
        );
      }
    }
  }
}
