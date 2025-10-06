#!/usr/bin/env node
const fs = require('fs')
const path = require('path')

const ROOT = path.resolve(__dirname, '..')
const INVENTORY = path.join(ROOT, 'inventory.json')
const ORDERS = path.join(ROOT, 'orders.json')
const BACKUP_DIR = path.join(ROOT, 'backups')

function ensureDir(dir) {
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true })
}

function backupFile(filePath) {
  if (!fs.existsSync(filePath)) return
  ensureDir(BACKUP_DIR)
  const ts = new Date().toISOString().replace(/[:.]/g, '-')
  const base = path.basename(filePath)
  const dest = path.join(BACKUP_DIR, `${base}.${ts}.bak`)
  fs.copyFileSync(filePath, dest)
  console.log(`Backed up ${base} -> ${dest}`)
}

function randomFloat(min, max, dec = 2) {
  return +(Math.random() * (max - min) + min).toFixed(dec)
}

function generateInventory(count = 8) {
  const names = ['Manzana Roja','Plátano','Lechuga','Tomate','Cebolla','Zanahoria','Pepino','Pimiento','Ajo','Perejil']
  const products = []
  for (let i = 0; i < count; i++) {
    const id = `prod-${String(i+1).padStart(3,'0')}`
    const name = names[i % names.length]
    const price = randomFloat(0.5, 3.5, 2)
    const qty = randomFloat(5, 100, 2)
    products.push({ id, name, price, quantity: qty })
  }
  return { products }
}

function generateOrders(inv, count = 5) {
  const orders = []
  for (let i = 0; i < count; i++) {
    const items = []
    const itemsCount = Math.floor(Math.random() * 3) + 1
    for (let j = 0; j < itemsCount; j++) {
      const p = inv.products[Math.floor(Math.random() * inv.products.length)]
      const qty = randomFloat(0.1, Math.min(5, p.quantity), 2)
      items.push({ productId: p.id, quantity: qty })
    }
    const total = items.reduce((s, it) => {
      const p = inv.products.find(x => x.id === it.productId)
      return s + (p.price * it.quantity)
    }, 0)
    const paid = +(total + randomFloat(0, 5, 2)).toFixed(2)
    orders.push({ id: `order-${Date.now()}-${i}`, items, total: +total.toFixed(2), paid, timestamp: Date.now() })
  }
  return { orders }
}

function main() {
  const invCount = parseInt(process.argv[2] || '8', 10)
  const ordCount = parseInt(process.argv[3] || '5', 10)

  backupFile(INVENTORY)
  backupFile(ORDERS)

  const inv = generateInventory(invCount)
  fs.writeFileSync(INVENTORY, JSON.stringify(inv, null, 2), 'utf8')
  console.log(`Wrote ${INVENTORY}`)

  const ord = generateOrders(inv, ordCount)
  fs.writeFileSync(ORDERS, JSON.stringify(ord, null, 2), 'utf8')
  console.log(`Wrote ${ORDERS}`)
}

main()
