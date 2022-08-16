import { GameObject } from "./GameObject";
import { Cell } from "./Cell";

export class Snake extends GameObject {
  constructor(info, gamemap) {
    super();

    this.id = info.id;
    this.color = info.color;
    this.gamemap = gamemap;

    this.cells = [new Cell(info.r, info.c)]; // To store the snake's body, cells[0] is the head of the snake

    this.speed = 1;
  }

  start() {}

  update_move() {}

  update() {
    this.update_move();
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
