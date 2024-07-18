import { Page, Text, View, Document, StyleSheet, PDFDownloadLink } from '@react-pdf/renderer';

// Styles for PDF document
const styles = StyleSheet.create({
    page: {
        flexDirection: 'column',
        backgroundColor: '#fff',
        padding: 20,
    },
    header: {
        fontSize: 18,
        marginBottom: 20,
        textAlign: 'center',
        textTransform: 'uppercase',
        fontWeight: 'bold',
    },
    section: {
        marginBottom: 10,
    },
    sectionTitle: {
        fontSize: 14,
        marginBottom: 5,
        fontWeight: 'bold',
    },
    text: {
        fontSize: 12,
        marginBottom: 5,
    },
    boldText: {
        fontSize: 12,
        marginBottom: 5,
        fontWeight: 'bold',
    },
    flexRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
    },
    table: {
        display: 'table',
        width: '100%',
        borderStyle: 'solid',
        borderWidth: 1,
        borderRightWidth: 0,
        borderBottomWidth: 0,
    },
    tableRow: {
        flexDirection: 'row',
    },
    tableColLabel: {
        width: '50%',
        borderStyle: 'solid',
        borderWidth: 1,
        borderLeftWidth: 0,
        borderTopWidth: 0,
        backgroundColor: '#f2f2f2',
        textAlign: 'left',
        padding: 5,
    },
    tableColValue: {
        width: '50%',
        borderStyle: 'solid',
        borderWidth: 1,
        borderLeftWidth: 0,
        borderTopWidth: 0,
        textAlign: 'right',
        padding: 5,
    },
    tableColHeader: {
        width: '25%',
        borderStyle: 'solid',
        borderWidth: 1,
        borderLeftWidth: 0,
        borderTopWidth: 0,
        backgroundColor: '#f2f2f2',
        textAlign: 'center',
        padding: 5,
    },
    tableCol: {
        width: '25%',
        borderStyle: 'solid',
        borderWidth: 1,
        borderLeftWidth: 0,
        borderTopWidth: 0,
        textAlign: 'center',
        padding: 5,
    },
    tableCellHeader: {
        fontSize: 12,
        fontWeight: 'bold',
    },
    tableCell: {
        fontSize: 10,
    },
    commentsSection: {
        marginTop: 10,
        padding: 10,
        borderTop: '1px solid #000',
        borderBottom: '1px solid #000',
    },
    commentText: {
        fontSize: 12,
        marginBottom: 5,
    },
    boldCommentText: {
        fontSize: 12,
        fontWeight: 'bold',
        marginBottom: 5,
    },
    sectionTotalQuanity: {
        fontSize: 12,
        fontWeight: 'bold',
        textAlign: 'right',
        marginTop: 10,
    },
    footerSection: {
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: 20,
    },
    footerText: {
        fontSize: 12,
        marginBottom: 5,
    },
    boldFooterText: {
        fontSize: 12,
        fontWeight: 'bold',
        marginBottom: 5,
    },
    tableColComment: {
        width: '100%',
        height: 100,
        borderStyle: 'solid',
        borderWidth: 1,
        borderLeftWidth: 0,
        borderTopWidth: 0,
        padding: 5,
    },
    tableColHeaderComment: {
        width: '100%',
        borderStyle: 'solid',
        borderWidth: 1,
        borderLeftWidth: 0,
        borderTopWidth: 0,
        backgroundColor: '#f2f2f2',
        textAlign: 'center',
        padding: 5,
    },
});

const ExportPDFDocument = ({ exportData, exportProducts }) => (
    <Document>
        <Page size="A4" style={styles.page}>
            <Text style={styles.header}>WAREHOUSE {exportData.warehouseFrom.name}'s EXPORT</Text>

            <View style={styles.section}>
                <Text style={styles.sectionTitle}>Export Details</Text>
                <View style={styles.table}>
                    <View style={styles.tableRow}>
                        <View style={styles.tableColLabel}>
                            <Text style={styles.tableCellHeader}>Description</Text>
                        </View>
                        <View style={styles.tableColValue}>
                            <Text style={styles.tableCell}>{exportData.description}</Text>
                        </View>
                    </View>
                    <View style={styles.tableRow}>
                        <View style={styles.tableColLabel}>
                            <Text style={styles.tableCellHeader}>Status</Text>
                        </View>
                        <View style={styles.tableColValue}>
                            <Text style={styles.tableCell}>{exportData.status}</Text>
                        </View>
                    </View>
                    <View style={styles.tableRow}>
                        <View style={styles.tableColLabel}>
                            <Text style={styles.tableCellHeader}>Export Date</Text>
                        </View>
                        <View style={styles.tableColValue}>
                            <Text style={styles.tableCell}>{new Date(exportData.exportDate).toLocaleDateString()}</Text>
                        </View>
                    </View>
                </View>
            </View>

            <View style={styles.section}>
                <Text style={styles.sectionTitle}>{exportData.warehouseTo ? 'Warehouse To' : 'Customer Details'}</Text>
                <View style={styles.table}>
                    {exportData.warehouseTo ? (
                        <>
                            <View style={styles.tableRow}>
                                <View style={styles.tableColLabel}>
                                    <Text style={styles.tableCellHeader}>Warehouse To</Text>
                                </View>
                                <View style={styles.tableColValue}>
                                    <Text style={styles.tableCell}>{exportData.warehouseTo.name}</Text>
                                </View>
                            </View>
                            <View style={styles.tableRow}>
                                <View style={styles.tableColLabel}>
                                    <Text style={styles.tableCellHeader}>Address</Text>
                                </View>
                                <View style={styles.tableColValue}>
                                    <Text style={styles.tableCell}>{exportData.warehouseTo.address}</Text>
                                </View>
                            </View>
                        </>
                    ) : (
                        <>
                            <View style={styles.tableRow}>
                                <View style={styles.tableColLabel}>
                                    <Text style={styles.tableCellHeader}>Customer Name</Text>
                                </View>
                                <View style={styles.tableColValue}>
                                    <Text style={styles.tableCell}>{exportData.customer.name}</Text>
                                </View>
                            </View>
                            <View style={styles.tableRow}>
                                <View style={styles.tableColLabel}>
                                    <Text style={styles.tableCellHeader}>Customer Email</Text>
                                </View>
                                <View style={styles.tableColValue}>
                                    <Text style={styles.tableCell}>{exportData.customer.email}</Text>
                                </View>
                            </View>
                            <View style={styles.tableRow}>
                                <View style={styles.tableColLabel}>
                                    <Text style={styles.tableCellHeader}>Customer Phone</Text>
                                </View>
                                <View style={styles.tableColValue}>
                                    <Text style={styles.tableCell}>{exportData.customer.phone}</Text>
                                </View>
                            </View>
                            <View style={styles.tableRow}>
                                <View style={styles.tableColLabel}>
                                    <Text style={styles.tableCellHeader}>Customer Address</Text>
                                </View>
                                <View style={styles.tableColValue}>
                                    <Text style={styles.tableCell}>{exportData.customer.address}</Text>
                                </View>
                            </View>
                        </>
                    )}
                </View>
            </View>

            <View style={styles.section}>
                <Text style={styles.sectionTitle}>Export Products</Text>
                <View style={styles.table}>
                    <View style={styles.tableRow}>
                        <View style={styles.tableColHeader}>
                            <Text style={styles.tableCellHeader}>Product Name</Text>
                        </View>
                        <View style={styles.tableColHeader}>
                            <Text style={styles.tableCellHeader}>Category</Text>
                        </View>
                        <View style={styles.tableColHeader}>
                            <Text style={styles.tableCellHeader}>Quantity</Text>
                        </View>
                        <View style={styles.tableColHeader}>
                            <Text style={styles.tableCellHeader}>Expiration Date</Text>
                        </View>
                    </View>
                    {exportProducts.map((product, index) => (
                        <View style={styles.tableRow} key={index}>
                            <View style={styles.tableCol}>
                                <Text style={styles.tableCell}>{product.product.name}</Text>
                            </View>
                            <View style={styles.tableCol}>
                                <Text style={styles.tableCell}>{product.product.category.name}</Text>
                            </View>
                            <View style={styles.tableCol}>
                                <Text style={styles.tableCell}>{product.quantity}</Text>
                            </View>
                            <View style={styles.tableCol}>
                                <Text style={styles.tableCell}>{new Date(product.expiredAt).toLocaleDateString()}</Text>
                            </View>
                        </View>
                    ))}
                </View>
                <Text style={styles.sectionTotalQuanity}>Total Quantity: {
                    exportProducts.reduce((acc, product) => acc + product.quantity, 0)
                }</Text>

            </View>
            <View style={styles.commentsSection}>
                <View style={styles.table}>
                    <View style={styles.tableRow}>
                        <View style={styles.tableColHeaderComment}>
                            <Text style={styles.tableCellHeader}>Comment</Text>
                        </View>
                    </View>
                    <View style={styles.tableRow}>
                        <View style={styles.tableColComment}>
                        </View>
                    </View>
                </View>
            </View>
            <View style={styles.footerSection}>
                <Text style={styles.footerText}>If you have any questions or concerns, please contact</Text>
                <Text style={styles.boldFooterText}>Thank you for your business!</Text>
            </View>
        </Page>
    </Document>
);

export default ExportPDFDocument;
