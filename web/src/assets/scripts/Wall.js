import { GameObject } from "./GameObject";

export class Wall extends GameObject {
  constructor(r, c, gamemap) {
    super();

    this.r = r;
    this.c = c;
    this.gamemap = gamemap;
    this.color = "#B37226";
  }

  update() {
    this.render();
  }

  render() {
    const unit = this.gamemap.unit;
    const ctx = this.gamemap.ctx;

    ctx.fillStyle = this.color;
    ctx.fillRect(this.c * unit, this.r * unit, unit, unit);
  }
}
