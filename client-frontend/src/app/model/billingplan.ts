export interface BillingPlan {
    id: number;
	paymentAmount: number;
	paymentCurrency: string;
	frequency: string;
    type: string;
}
