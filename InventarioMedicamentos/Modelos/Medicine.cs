using System;

namespace InventarioMedicamentos.Modelos
{
    public class Medicine
    {
        public string Name { get; set; }
        public string Lot { get; set; }
        public DateTime ExpirationDate { get; set; }

        public Medicine(string name, string lot, DateTime expirationDate)
        {
            Name = name;
            Lot = lot;
            ExpirationDate = expirationDate;
        }

        public override string ToString()
        {
            return $"Nombre: {Name}, Lote: {Lot}, Vencimiento: {ExpirationDate:dd/MM/yyyy}";
        }
    }
}
