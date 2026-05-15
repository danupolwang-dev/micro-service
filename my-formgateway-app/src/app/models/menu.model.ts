export interface Menu {
  id: number;
  name: string;
  path: string;
  icon: string;
  displayOrder: number;
  // Optional: Add this if you plan to implement sub-menus in the future
  // children?: Menu[];
}
